package com.javadEsl.imageSearchApp.data


import com.google.gson.annotations.SerializedName

data class Links(
    val download: String?,
    @SerializedName("download_location")
    val downloadLocation: String?,
    val html: String?,
    val self: String?
)