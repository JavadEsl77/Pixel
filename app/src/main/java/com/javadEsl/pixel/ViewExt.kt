package com.javadEsl.pixel

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible

/**
 * set view visibility visible
 */
fun View.show(alpha: Float = 1f) {
    isVisible = true
    this.alpha = alpha
}

/**
 * set view visibility gone
 */
fun View.hide() {
    isGone = true
}

/**
 * set view visibility invisible
 */
fun View.invisible() {
    isInvisible = true
}

/**
 * expand view
 *
 * @param duration
 * @param listener
 */
fun View.expand(
    duration: Long? = null,
    listener: Animation.AnimationListener? = null
) {
    val matchParentMeasureSpec =
        View.MeasureSpec.makeMeasureSpec((parent as View).width, View.MeasureSpec.EXACTLY)
    val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    measure(matchParentMeasureSpec, wrapContentMeasureSpec)
    val targetHeight = measuredHeight

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    layoutParams.height = 1
    isVisible = true
    val animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            layoutParams.height = if (interpolatedTime == 1f)
                ViewGroup.LayoutParams.WRAP_CONTENT
            else
                (targetHeight * interpolatedTime).toInt()
            requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }


    if (duration != null) {
        animation.duration = duration
    } else {
        // 1dp/ms
        animation.duration =
            (targetHeight / context.resources.displayMetrics.density).toInt().toLong()
    }

    animation.setAnimationListener(listener)
    startAnimation(animation)
}

/**
 * collapse view
 *
 * @param listener
 */
fun View.collapse(listener: Animation.AnimationListener? = null) {
    val initialHeight = measuredHeight

    val animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1f) {
                isGone = true
            } else {
                layoutParams.height =
                    initialHeight - (initialHeight * interpolatedTime).toInt()
                requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }

    // 1dp/ms
    animation.duration = (initialHeight / context.resources.displayMetrics.density).toInt().toLong()
    animation.setAnimationListener(listener)
    startAnimation(animation)
}