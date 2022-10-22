package com.javadEsl.pixel.ui

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.javadEsl.pixel.BuildConfig
import com.javadEsl.pixel.R
import dagger.hilt.android.AndroidEntryPoint
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.TapsellPlusInitListener
import ir.tapsell.plus.model.AdNetworkError
import ir.tapsell.plus.model.AdNetworks


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nightModeFlags = this.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                val wic = WindowInsetsControllerCompat(
                    this.window,
                    this.window.decorView
                )
                wic.isAppearanceLightStatusBars = false
            }
            Configuration.UI_MODE_NIGHT_NO ->{
                val wic = WindowInsetsControllerCompat(
                    this.window,
                    this.window.decorView
                )
                wic.isAppearanceLightStatusBars = true
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {

            }
        }

        this.window.statusBarColor = ContextCompat.getColor(
            this,
            R.color.status_bar_color
        )

        TapsellPlus.initialize(this, BuildConfig.TAPSELL_KEY,
            object : TapsellPlusInitListener {
                override fun onInitializeSuccess(adNetworks: AdNetworks) {
                    Log.d("onInitializeSuccess", adNetworks.name)
                }

                override fun onInitializeFailed(
                    adNetworks: AdNetworks,
                    adNetworkError: AdNetworkError
                ) {
                    Log.e(
                        "onInitializeFailed",
                        "ad network: " + adNetworks.name + ", error: " + adNetworkError.errorMessage
                    )
                }
            })

    }


}