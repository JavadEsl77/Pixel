package com.javadEsl.pixel.api

import com.javadEsl.pixel.data.model.search.PixelPhoto


data class PixelResponse(
    val results: List<PixelPhoto>,
)