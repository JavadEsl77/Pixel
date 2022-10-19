package com.javadEsl.pixel.api


data class ApiResponse<T>(
    val `data`: T?,
    val message: String?,
    val success: Boolean
)