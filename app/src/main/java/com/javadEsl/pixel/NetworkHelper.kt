package com.javadEsl.pixel

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val CONNECTED_ACTION = "network.connected"
        const val DISCONNECTED_ACTION = "network.disconnected"
    }


    fun hasInternetConnection(
        url: String = "https://www.instagram.com/",
        callback: (Boolean) -> Unit
    ) {
        CoroutineScope(context = Dispatchers.IO).launch {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build()
                val request = Request.Builder()
                    .url(URL(url))
                    .get()
                    .build()
                client.newCall(request).execute()

                callback.invoke(true)
                context.sendBroadcast(Intent().apply {
                    action = CONNECTED_ACTION
                })
            } catch (e: IOException) {
                callback.invoke(false)
                context.sendBroadcast(Intent().apply {
                    action = DISCONNECTED_ACTION
                })
            }
        }
    }

    fun checkConnection(): Boolean {
        val connectionManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConnection = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileDataConnection = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        if (return wifiConnection?.isConnectedOrConnecting == true || (mobileDataConnection?.isConnectedOrConnecting == true)) true


    }

}