package com.javadEsl.pixel.data.topics


import com.google.gson.annotations.SerializedName

data class TopicsModelItem(
    @SerializedName("cover_photo")
    val coverPhoto: CoverPhoto,
    @SerializedName("current_user_contributions")
    val currentUserContributions: List<Any>,
    @SerializedName("description")
    val description: String,
    @SerializedName("direct_ads")
    val directAds: Boolean,
    @SerializedName("ends_at")
    val endsAt: String?,
    @SerializedName("featured")
    val featured: Boolean,
    @SerializedName("id")
    val id: String,
    @SerializedName("links")
    val links: LinksXX,
    @SerializedName("only_submissions_after")
    val onlySubmissionsAfter: Any?,
    @SerializedName("owners")
    val owners: List<Owner>,
    @SerializedName("preview_photos")
    val previewPhotos: List<PreviewPhoto>,
    @SerializedName("published_at")
    val publishedAt: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("starts_at")
    val startsAt: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("total_current_user_submissions")
    val totalCurrentUserSubmissions: Any?,
    @SerializedName("total_photos")
    val totalPhotos: Int,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("visibility")
    val visibility: String
)