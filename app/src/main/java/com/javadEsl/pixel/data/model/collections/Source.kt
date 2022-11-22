package com.javadEsl.pixel.data.model.collections


import com.google.gson.annotations.SerializedName

data class Source(
    val ancestry: Ancestry?,
    @SerializedName("cover_photo")
    val coverPhoto: CoverPhotoX?,
    val description: String?,
    @SerializedName("meta_description")
    val metaDescription: String?,
    @SerializedName("meta_title")
    val metaTitle: String?,
    val subtitle: String?,
    val title: String?
)