package com.javadEsl.pixel.data.model.detail


import com.google.gson.annotations.SerializedName

data class CurrentUserCollections(
    @SerializedName("cover_photo")
    val coverPhoto: Any?,
    val id: Int?,
    @SerializedName("last_collected_at")
    val lastCollectedAt: String?,
    @SerializedName("published_at")
    val publishedAt: String?,
    val title: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    val user: Any?
)