package com.javadEsl.pixel.data.model.topics


import com.google.gson.annotations.SerializedName

data class CoverPhoto(
    @SerializedName("alt_description")
    val altDescription: String?,
    @SerializedName("blur_hash")
    val blurHash: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("current_user_collections")
    val currentUserCollections: List<Any>,
    @SerializedName("description")
    val description: String?,
    @SerializedName("height")
    val height: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("liked_by_user")
    val likedByUser: Boolean,
    @SerializedName("likes")
    val likes: Int,
    @SerializedName("links")
    val links: com.javadEsl.pixel.data.model.topics.Links,
    @SerializedName("promoted_at")
    val promotedAt: String?,
    @SerializedName("sponsorship")
    val sponsorship: Any?,
    @SerializedName("topic_submissions")
    val topicSubmissions: com.javadEsl.pixel.data.model.topics.TopicSubmissions,
    val premium: Boolean? = null,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("urls")
    val urls: com.javadEsl.pixel.data.model.topics.Urls,
    @SerializedName("user")
    val user: com.javadEsl.pixel.data.model.topics.User,
    @SerializedName("width")
    val width: Int
)