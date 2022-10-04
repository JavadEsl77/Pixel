package com.javadEsl.pixel.data.search

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PixelPhoto(
    val id: String,
    val likes: Int? = null,
    val created_at: String? = null,
    val color: String? = null,
    val user: PixelUser? = null,
    val description: String? = null,
    val urls: PixelPhotoUrls? = null,
    val isAdvertisement: Boolean = false
) : Parcelable {

    @Parcelize
    data class PixelPhotoUrls(
        val raw: String?,
        val full: String?,
        val regular: String?,
        val small: String?,
        val thumb: String?
    ) : Parcelable

    @Parcelize
    data class PixelUser(
        val name: String?,
        val username: String?,
        val portfolio_url: String?,
        val profile_image: PixelProfileImage?
    ) : Parcelable {
        val attributionUrl get() = "https://us.mahdi-saberi.ir/$username?utm_source=ImageSearchApp&utm_medium=referral"
    }

    @Parcelize
    data class PixelProfileImage(
        val small: String?,
        val medium: String?,
        val large: String?,
    ) : Parcelable

}

val String.convertedUrl: String
    get() = this.replace("https://images.unsplash.com/", "https://im.mahdi-saberi.ir/")

