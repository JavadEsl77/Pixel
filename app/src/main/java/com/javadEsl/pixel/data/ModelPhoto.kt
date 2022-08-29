package com.javadEsl.pixel.data


import com.google.gson.annotations.SerializedName

data class ModelPhoto(
    val color: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    val downloads: Int?,
    val exif: Exif?,
    val height: Int?,
    val id: String?,
    val likes: Int?,
    val links: Links?,
    val location: Location?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("current_user_collections")
    val currentUserCollections: List<CurrentUserCollections>?,
    val urls: Urls?,
    val user: User?,
    val views: Int?,
    val width: Int?
)