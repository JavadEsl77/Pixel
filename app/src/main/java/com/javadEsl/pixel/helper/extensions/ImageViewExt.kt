package com.javadEsl.pixel

import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.widget.ImageViewCompat
import com.bumptech.glide.Glide

/**
 * @copyright This source code written by Majid Arabi and
 * you don't access to use any part of this in another project
 * or publish that for any person.
 * Date: 1/20/2021 AD
 */

fun ImageView.setTint(@ColorRes color: Int) = ImageViewCompat.setImageTintList(
    this,
    ColorStateList.valueOf(context.getResColor(color))
)

/**
 * Load as circle
 *
 * @param file
 */
fun ImageView.loadAsCircle(
    file: Any?,
) = Glide.with(this)
    .load(file)
    .circleCrop()
    .into(this)
