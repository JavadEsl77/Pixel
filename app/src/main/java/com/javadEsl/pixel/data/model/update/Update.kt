package com.javadEsl.pixel.data.model.update

data class Update(
    val ignoreButtonText: String?,
    val isForce: Boolean?,
    val links: List<Link>?,
    val message: String?,
    val splashImageUrl: String?,
    val versionCode: Int?,
    val versionName: String?
)