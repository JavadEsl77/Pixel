package com.javadEsl.pixel.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnsplashPhoto(
    val id: String,
    val likes: Int? = null,
    val created_at: String? = null,
    val color: String? = null,
    val user: UnsplashUser? = null,
    val description: String? = null,
    val urls: UnsplashPhotoUrls? = null,
    val isAdvertisement: Boolean = false
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

