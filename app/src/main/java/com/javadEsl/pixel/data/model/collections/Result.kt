package com.javadEsl.pixel.data.model.collections


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("cover_photo")
    val coverPhoto: CoverPhoto?,
    val curated: Boolean?,
    val description: Any?,
    val featured: Boolean?,
    val id: String?,
    @SerializedName("last_collected_at")
    val lastCollectedAt: String?,
    val links: LinksXX?,
    @SerializedName("preview_photos")
    val previewPhotos: List<PreviewPhoto>?,
    val `private`: Boolean?,
    @SerializedName("published_at")
    val publishedAt: String?,
    @SerializedName("share_key")
    val shareKey: String?,
    val tags: List<Tag>?,
    val title: String?,
    @SerializedName("total_photos")
    val totalPhotos: Int?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    val user: UserXX?
)