package com.javadEsl.pixel.data.model.topics


import com.google.gson.annotations.SerializedName

data class TopicsModelItem(
    @SerializedName("cover_photo")
    val coverPhoto: com.javadEsl.pixel.data.model.topics.CoverPhoto? = null,
    @SerializedName("current_user_contributions")
    val currentUserContributions: List<Any>? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("direct_ads")
    val directAds: Boolean? = null,
    @SerializedName("ends_at")
    val endsAt: String? = null,
    @SerializedName("featured")
    val featured: Boolean? = null,
    @SerializedName("id")
    val id: String,
    @SerializedName("links")
    val links: com.javadEsl.pixel.data.model.topics.LinksXX? = null,
    @SerializedName("only_submissions_after")
    val onlySubmissionsAfter: Any? = null,
    @SerializedName("owners")
    val owners: List<com.javadEsl.pixel.data.model.topics.Owner>? = null,
    @SerializedName("preview_photos")
    val previewPhotos: List<com.javadEsl.pixel.data.model.topics.PreviewPhoto>? = null,
    @SerializedName("published_at")
    val publishedAt: String? = null,
    @SerializedName("slug")
    val slug: String? = null,
    @SerializedName("starts_at")
    val startsAt: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("total_current_user_submissions")
    val totalCurrentUserSubmissions: Any? = null,
    @SerializedName("total_photos")
    val totalPhotos: Int? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("visibility")
    val visibility: String? = null,
    var isSelected: Boolean = false
) {
    object Type {
        const val USER = "user_type"
    }
}