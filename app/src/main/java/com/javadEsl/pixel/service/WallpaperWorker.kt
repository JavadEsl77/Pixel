package com.javadEsl.pixel.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.javadEsl.pixel.R
import com.javadEsl.pixel.ui.MainActivity
import java.io.File


class WallpaperWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private val sharedPreference by lazy {
        applicationContext.getSharedPreferences("wallpaperWorkerPref", Context.MODE_PRIVATE)
    }

    companion object {
        const val CHANNEL_ID = "channel_id"
        const val NOTIFICATION_ID = 1

    }

    override fun doWork(): Result {
        //showNotification()
        setAutoWallpaper()
        return Result.success()
    }

    private fun setAutoWallpaper() {
        val fileName = sharedPreference.getString("wallpaperFile", null)
        val files = getDownloadPictures(applicationContext.getString(R.string.app_name))

        if (fileName.isNullOrBlank() && files.isNotEmpty()) {
            setWall(files.first())
            return
        }

        if (files.isNotEmpty()) {
            var imgFile = files.find { it.name == fileName }
            if (imgFile != null) {
                imgFile = files.getOrNull(files.indexOf(imgFile) + 1)
            }

            if (imgFile == null) {
                imgFile = files.first()
            }

            setWall(imgFile)
            saveFileName(imgFile.name)
        }
    }

    private fun saveFileName(name: String) {
        sharedPreference.edit().apply {
            putString("wallpaperFile", name)
            apply()
        }
    }

    private fun setWall(file: File) {
        val bmOptions = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeFile(file.toString(), bmOptions)

        val wallpaperManager =
            WallpaperManager.getInstance(applicationContext.applicationContext)
        wallpaperManager.setWallpaperOffsetSteps(1f, 1f);
        wallpaperManager.setBitmap(bitmap)
        saveFileName(file.name)
    }

    private fun getDownloadPictures(folderName: String): List<File> {
        val root = Environment.getExternalStorageDirectory()
        val myDir = File("${root}/${folderName}")
        if (myDir.exists()) {
            val files = myDir.listFiles()
            val images = buildList {
                files?.forEach {
                    if (it?.isFile == true) add(it)
                    else it?.listFiles()?.forEach { subFile ->
                        if (subFile.isFile) add(subFile)
                    }
                }
            }

            return images
        }
        return emptyList()
    }


    private fun showNotification() {

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0, intent, 0
        )


        val notification = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("New Task")
            .setContentText("Subscribe on the channel")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelName = "Channel Name"
            val channelDescription = "Channel Description"
            val channelImportance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANNEL_ID, channelName, channelImportance).apply {
                description = channelDescription
            }

            val notificationManager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }


        with(NotificationManagerCompat.from(applicationContext)) {
            notify(NOTIFICATION_ID, notification.build())
        }

    }
}