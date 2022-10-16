package com.javadEsl.pixel

import android.app.Service
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.IBinder
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.huxq17.download.Pump
import com.huxq17.download.config.DownloadConfig


class DownloaderService : Service() {

    companion object {
        private var isRunning = false
        private var url: String = ""
        private var path: String = ""
        fun start(context: Context, url: String, path: String) {
            this.url = url
            this.path = path
            val intent = Intent(context, DownloaderService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        starForegroundDownload("0")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning){
            starForegroundDownload("0")
        }
        downloadImage()
        return START_STICKY
    }

    private fun starForegroundDownload(
        progress: String
    ) {
        val filename: String = path.substring(path.lastIndexOf("/") + 1)
        startForeground(
            BuildConfig.APPLICATION_ID.hashCode(),
            DownloadNotificationHelper.createNotification(
                context = this.applicationContext,
                message = "% $progress",
                title = " Download $filename",
                progress = progress.toInt()
            )
        )
        isRunning = true
    }

    override fun onDestroy() {
        isRunning = false
    }

    private fun downloadImage() {

        val soundUri = "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${this.applicationContext.packageName}/${R.raw.camera}".toUri()
        val r = RingtoneManager.getRingtone(applicationContext, soundUri)
        r.play()

        DownloadConfig.newBuilder()
            .setMaxRunningTaskNum(2)
            .setMinUsableStorageSpace(4 * 1024L)
            .build()

        Pump.newRequest(url, path)
            .listener(object : com.huxq17.download.core.DownloadListener() {
                override fun onProgress(progress: Int) {
                    starForegroundDownload(progress.toString())
                }

                override fun onSuccess() {
                    stopForeground(true)
                    stopSelf()
                    isRunning = false
                    r.stop()

                }

                override fun onFailed() {
                    isRunning = false
                    r.stop()
                    stopForeground(true)
                    stopSelf()
                }
            })
            .forceReDownload(true)
            .threadNum(3)
            .setRetry(3, 200)
            .submit()
    }


}