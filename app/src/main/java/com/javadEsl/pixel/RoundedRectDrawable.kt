package com.javadEsl.pixel

import android.graphics.drawable.PaintDrawable

class  RoundedRectDrawable(color: Int, radius: Float) : PaintDrawable(color) {
    init {
        setCornerRadius(radius)
    }
}