package com.javadEsl.imageSearchApp.api

import com.javadEsl.imageSearchApp.data.UnsplashPhoto

data class UnsplashResponse(
    val results: List<UnsplashPhoto>,
)