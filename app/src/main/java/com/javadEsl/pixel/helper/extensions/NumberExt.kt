package com.javadEsl.pixel.helper.extensions

import android.content.res.Resources
import android.graphics.Color
import java.text.DecimalFormat
import java.util.*
import kotlin.math.sqrt


val Int.isBrightColor: Boolean
    get() {
        val color = this
        if (android.R.color.transparent == color) return true
        var rtnValue = false
        val rgb = intArrayOf(Color.red(color), Color.green(color), Color.blue(color))
        val brightness = sqrt(
            rgb[0] * rgb[0] * .241 + (rgb[1]
                    * rgb[1] * .691) + rgb[2] * rgb[2] * .068
        ).toInt()

        // color is light
        if (brightness >= 200) {
            rtnValue = true
        }
        return rtnValue
    }


fun Number?.toDecimal(): String {
    if (this == null) return ""
    return DecimalFormat.getInstance(Locale.US).format(this)
}

/**
 * Round to
 *
 * @param numFractionDigits
 */
internal fun Number.roundTo(
    numFractionDigits: Int = 2
) = "%.${numFractionDigits}f".format(this, Locale.ENGLISH)


/**
 * convert px to dp
 */
val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

/**
 * convert dp to px
 */
val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


/**
 * convert time in millis to readable time format
 * ex: 10 : 05 : 58 : 60
 * day : hour : minutes : seconds
 *
 * @return
 */
fun Long.toTime(giveDay: Boolean = false): String {
    val timeMs = this
    if (timeMs <= 0 || timeMs >= 30 * 24 * 60 * 60 * 1000L)
        return "00:00"

    val totalSeconds = timeMs / 1000
    val seconds = (totalSeconds % 60)
    val minutes = (totalSeconds % 3600 / 60)
    val hours = (totalSeconds % 86400 / 3600)
    val days = (totalSeconds % (86400 * 30) / 86400)

    val mFormatter = Formatter(StringBuilder(), Locale.getDefault())

    return when {
        days > 0  -> {
            if (giveDay)
                mFormatter.format("%d", days).toString()
            else
                mFormatter.format("%d:%d:%02d:%02d", days, hours, minutes, seconds).toString()
        }
        hours > 0 ->
            mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        else      ->
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
    }
}
