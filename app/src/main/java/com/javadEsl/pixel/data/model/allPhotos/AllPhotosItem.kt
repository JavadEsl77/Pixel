package com.javadEsl.pixel.data.model.allPhotos


import com.google.gson.annotations.SerializedName

data class AllPhotosItem(
    @SerializedName("alt_description")
    val altDescription: Any? = null,
    @SerializedName("blur_hash")
    val blurHash: String? = null,
    val color: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("current_user_collections")
    val currentUserCollections: List<Any>? = null,
    val description: String? = null,
    val height: Int? = null,
    val id: String? = null,
    @SerializedName("liked_by_user")
    val likedByUser: Boolean? = null,
    val likes: Int? = null,
    val links: Links? = null,
    @SerializedName("promoted_at")
    val promotedAt: String? = null,
    val sponsorship: Sponsorship? = null,
    @SerializedName("topic_submissions")
    val topicSubmissions: TopicSubmissions? = null,
    val premium:Boolean?=null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    val urls: Urls? = null,
    val user: User? = null,
    val width: Int? = null,
    val isAdvertisement: Boolean = false
)