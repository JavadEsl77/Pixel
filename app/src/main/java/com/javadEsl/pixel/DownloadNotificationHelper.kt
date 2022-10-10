package com.javadEsl.pixel

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

object DownloadNotificationHelper {
    const val DOWNLOADER_CHANNEL_ID = BuildConfig.APPLICATION_ID

    @SuppressLint("RemoteViewLayout")
    fun createNotification(
        context: Context,
        message: String,
        title: String
    ): Notification {
        val notificationManager =
            context.applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                DOWNLOADER_CHANNEL_ID,
                "downloader",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "User for downloading photos"
            notificationManager.createNotificationChannel(channel)

        }

        val notification = NotificationCompat.Builder(context, DOWNLOADER_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_download_detail)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(
            BuildConfig.APPLICATION_ID.hashCode(),
            notification
        )

        return notification
    }

}