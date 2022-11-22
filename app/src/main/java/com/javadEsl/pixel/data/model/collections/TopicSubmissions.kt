package com.javadEsl.pixel.data.model.collections


import com.google.gson.annotations.SerializedName

data class TopicSubmissions(
    @SerializedName("act-for-nature")
    val actForNature: ActForNature?,
    val experimental: Experimental?,
    val nature: Nature?,
    @SerializedName("textures-patterns")
    val texturesPatterns: TexturesPatterns?,
    val wallpapers: Wallpapers?
)