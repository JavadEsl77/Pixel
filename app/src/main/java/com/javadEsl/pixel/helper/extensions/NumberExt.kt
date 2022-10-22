package com.javadEsl.pixel

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
