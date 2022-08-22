package com.javadEsl.imageSearchApp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnsplashPhoto(
    val id: String,
    val likes: Int?,
    val created_at: String?,
    val color: String?,
    val description: String?,
    val urls: UnsplashPhotoUrls?,
    val user: UnsplashUser?
) : Parcelable {

    @Parcelize
    data class UnsplashPhotoUrls(
        val raw: String?,
        val full: String?,
        val regular: String?,
        val small: String?,
        val thumb: String?
    ) : Parcelable

    @Parcelize
    data class UnsplashUser(
        val name: String?,
        val username: String?,
        val portfolio_url: String?,
        val profile_image: UnsplashProfileImage?
    ) : Parcelable {
        val attributionUrl get() = "https://us.mahdi-saberi.ir/$username?utm_source=ImageSearchApp&utm_medium=referral"
    }

    @Parcelize
    data class UnsplashProfileImage(
        val small: String?,
        val medium: String?,
        val large: String?,
    ) : Parcelable

}

val String.convertedUrl: String
    get() = this.replace("https://images.unsplash.com/", "https://im.mahdi-saberi.ir/")