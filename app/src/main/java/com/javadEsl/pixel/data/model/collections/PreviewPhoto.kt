package com.javadEsl.pixel.data.model.collections


import com.google.gson.annotations.SerializedName

data class PreviewPhoto(
    @SerializedName("blur_hash")
    val blurHash: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    val id: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    val urls: Urls?
)