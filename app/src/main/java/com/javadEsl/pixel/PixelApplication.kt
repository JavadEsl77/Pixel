package com.javadEsl.pixel

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
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