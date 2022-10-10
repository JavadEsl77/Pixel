package com.javadEsl.pixel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.multidex.MultiDex
import com.javadEsl.pixel.DownloadNotificationHelper.DOWNLOADER_CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp
import io.sentry.android.core.SentryAndroid
import io.sentry.android.core.SentryAndroidOptions

@HiltAndroidApp
class PixelApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)

    }

    override fun onCreate() {
        super.onCreate()
        setupSentry()
    }

    private fun setupSentry() {
        SentryAndroid.init(
            this
        ) { options: SentryAndroidOptions ->
            options.environment = "production"
        }
    }

}