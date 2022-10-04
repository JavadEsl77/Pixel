package com.javadEsl.pixel.data.detail


import com.google.gson.annotations.SerializedName

data class User(
    val bio: String?,
    @SerializedName("first_name")
    val firstName: String?,
    val id: String?,
    @SerializedName("instagram_username")
    val instagramUsername: String?,
    @SerializedName("last_name")
    val lastName: String?,
    val links: LinksX?,
    val location: String?,
    val name: String?,
    @SerializedName("portfolio_url")
    val portfolioUrl: String?,
    @SerializedName("profile_image")
    val profileImage: ProfileImage?,
    @SerializedName("total_collections")
    val totalCollections: Int?,
    @SerializedName("total_likes")
    val totalLikes: Int?,
    @SerializedName("total_photos")
    val totalPhotos: Int?,
    @SerializedName("twitter_username")
    val twitterUsername: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    val username: String?
)