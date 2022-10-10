package com.javadEsl.pixel

import android.R.attr
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.content.ContextCompat
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
        if (!isRunning) starForegroundDownload("$startId")
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
                message = "          - درصد پیشرفت دانلود ...   $progress٪",
                title = " در حل دانلود فایل  $filename"
            )
        )
        isRunning = true
    }

    override fun onDestroy() {
        isRunning = false
    }

    private fun downloadImage() {

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
                    Toast.makeText(
                        applicationContext,
                        R.string.string_alert_success_download,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onFailed() {

                }
            })
            .forceReDownload(true)
            .threadNum(3)
            .setRetry(3, 200)
            .submit()
    }


}