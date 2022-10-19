package com.javadEsl.pixel

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.widget.TextView
import com.tapadoo.alerter.Alerter

private const val ALERT_SHORT_DURATION = 3000L
private const val ALERT_LONG_DURATION = 5000L

/**
 * create custom alert using [Alerter] library
 *
 * @param message
 * @param duration
 */
private fun Activity.alert(
    message: String,
    duration: Long
) {
    if (message.isBlank()) return
    Alerter.create(this, R.layout.layout_alerter)
        .setDuration(duration)
        .enableSwipeToDismiss()
        .setLayoutGravity(Gravity.TOP)
        .setBackgroundColorRes(android.R.color.transparent)
        .also { alerter ->
            alerter.getLayoutContainer()?.let {
                val textView = it.findViewById<TextView>(R.id.tv_message)
                textView.text = message
            }
        }
        .show()
}

/**
 * show short alert with [Alerter] library
 *
 * @param message
 */
fun Activity.alert(message: String) =
    alert(message = message, duration = ALERT_SHORT_DURATION)

/**
 * show long alert with [Alerter] library
 *
 * @param message
 */
fun Activity.longAlert(message: String) =
    alert(message = message, duration = ALERT_LONG_DURATION)

/**
* Browse url
*
* @param url
*/
fun Activity.browseUrl(url: String?) {
    if (url.isNullOrEmpty()) {
        toast("لینک خالی میباشد!!!")
        return
    }
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url.normalized)))
    } catch (e: ActivityNotFoundException) {
        toast("مرورگری برای باز کردن لینک یافت نشد")
    }
}