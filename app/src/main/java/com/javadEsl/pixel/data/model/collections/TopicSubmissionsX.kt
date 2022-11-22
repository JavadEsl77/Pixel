package com.javadEsl.pixel.data.model.collections


import com.google.gson.annotations.SerializedName

data class TopicSubmissionsX(
    val animals: Animals?,
    val architecture: Architecture?,
    @SerializedName("architecture-interior")
    val architectureInterior: ArchitectureInterior?,
    @SerializedName("arts-culture")
    val artsCulture: ArtsCulture?,
    @SerializedName("color-of-water")
    val colorOfWater: ColorOfWater?,
    @SerializedName("current-events")
    val currentEvents: CurrentEvents?,
    val experimental: Experimental?,
    @SerializedName("fashion-beauty")
    val fashionBeauty: FashionBeauty?,
    val nature: Nature?,
    val people: People?,
    @SerializedName("textures-patterns")
    val texturesPatterns: TexturesPatterns?,
    val wallpapers: Wallpapers?
)