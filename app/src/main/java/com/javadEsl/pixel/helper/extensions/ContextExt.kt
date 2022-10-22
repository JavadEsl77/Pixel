package com.javadEsl.pixel.helper.extensions

import android.app.Activity
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import java.io.File
import kotlin.math.roundToInt

/**
 * @copyright This source code written by Majid Arabi and
 * you don't access to use any part of this in another project
 * or publish that for any person.
 * Date: 2/16/2021 AD
 */

/**
 * Get res color
 *
 * @param color
 */
fun Context.getResColor(@ColorRes color: Int) =
    ContextCompat.getColor(this, color)


/**
 * Get res drawable
 *
 * @param drawable
 */
fun Context.getResDrawable(@DrawableRes drawable: Int) =
    ContextCompat.getDrawable(this, drawable)


/**
 * Is online
 *
 * @return
 */
fun Context.isOnline(): Boolean {
    val connectivityManager =
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}


/**
 * show toast with custom layout
 *
 * @param message a message must be show
 * @param isShort duration of toast. default is short
 */
private fun Context.makeToast(
    message: String?,
    isShort: Boolean = true
) {

    val duration = if (isShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG

    Toast.makeText(this.applicationContext, "$message", duration).apply {
        setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 0)
        show()
    }

}

fun Context.toast(message: String?) =
    makeToast(message = message)

fun Context.toast(@StringRes message: Int) =
    makeToast(message = getString(message))

fun Context.longToast(message: String?) =
    makeToast(message = message, isShort = false)

fun Context.longToast(@StringRes message: Int) =
    makeToast(message = getString(message), isShort = false)

/**
 *  extension property to get screen width and height in dp
 */
val Context.screenSizeInDp: Point
    get() {
        val widthDp = resources.displayMetrics.run { widthPixels / density }
        val heightDp = resources.displayMetrics.run { heightPixels / density }

        return Point().apply {
            // screen width in dp
            x = widthDp.roundToInt()

            // screen height in dp
            y = heightDp.roundToInt()
        }
    }

/**
 * Hide keyboard
 *
 * @param view
 */
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Get cache files
 *
 * @return
 */
fun Context.getCacheFiles(): List<File> {
    val files = arrayListOf<File>()
    val dir = File(externalCacheDir, "")
    if (dir.exists()) dir.listFiles()?.forEach {
        files.add(it)
    }
    return files
}

/**
 * Get cache files name
 *
 * @return the list of file name
 */
fun Context.getCacheFilesName(): List<String> {
    val filesName = arrayListOf<String>()
    getCacheFiles().forEach {
        filesName.add(it.name)
    }
    return filesName
}

/**
 * Get notification manager
 */
fun Context.notificationManager() =
    this.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


/**
 * make call for number
 *
 * @param phone
 */
fun Context.call(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$phone")
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        toast("برنامه ای برای شماره گیری یافت نشد")
    }
}

/**
 * return true if in App's Battery settings "Not optimized" and false if "Optimizing battery use"
 */
fun Context.isIgnoringBatteryOptimizations(): Boolean {
    Thread.sleep(1000)
    val pm = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        return pm.isIgnoringBatteryOptimizations(packageName)
    return true
}

/**
 * Copy text to clipboard
 *
 * @param text
 */
fun Context.copyText(text: String?) {
    if (text.isNullOrBlank()) return
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
}

/**
 * check the package is installed
 * @param packageName
 */
fun Context.isPackageInstalled(packageName: String): Boolean = try {
    packageManager.getPackageInfo(packageName, 0)
    true
} catch (e: PackageManager.NameNotFoundException) {
    false
}
