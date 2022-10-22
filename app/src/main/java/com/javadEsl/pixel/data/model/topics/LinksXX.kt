package com.javadEsl.pixel.data.model.topics


import com.google.gson.annotations.SerializedName

data class LinksXX(
    @SerializedName("html")
    val html: String,
    @SerializedName("photos")
    val photos: String,
    @SerializedName("self")
    val self: String
)