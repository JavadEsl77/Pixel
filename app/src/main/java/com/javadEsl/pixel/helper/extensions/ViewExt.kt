package com.javadEsl.pixel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

const val ANIMATION_DURATION = 350L

/**
 * Fade in view
 *
 * @param listener
 * @param duration
 */
fun View.fadeIn(
    listener: Animator.AnimatorListener? = null,
    duration: Long = ANIMATION_DURATION
) {
    if (isVisible) return
    isVisible = true
    alpha = 0f
    animate()
        .setDuration(duration)
        .alpha(1f)
        .setListener(listener)
        .start()
}

/**
 * Fade out view
 *
 * @param listener
 * @param invisibleView
 * @param duration
 */
fun View.fadeOut(
    listener: Animator.AnimatorListener? = null,
    invisibleView: Boolean = false,
    duration: Long = ANIMATION_DURATION
) {
    if (!isVisible) return
    val mListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            if (invisibleView) invisible() else hide()
        }
    }
    alpha = 1f
    animate()
        .setDuration(duration)
        .alpha(0f)
        .setListener(listener ?: mListener)
        .start()
}


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

fun View.slideUp(animTime: Long, startOffSet: Long) {
    val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up).apply {
        duration = animTime
        interpolator = FastOutSlowInInterpolator()
        this.startOffset = startOffSet
    }
    startAnimation(slideUp)
}

