package com.javadEsl.pixel.data.model.topics


import com.google.gson.annotations.SerializedName

data class TopicSubmissions(
    @SerializedName("act-for-nature")
    val actForNature: com.javadEsl.pixel.data.model.topics.ActForNature?,
    @SerializedName("architecture")
    val architecture: com.javadEsl.pixel.data.model.topics.Architecture?,
    @SerializedName("arts-culture")
    val artsCulture: com.javadEsl.pixel.data.model.topics.ArtsCulture?,
    @SerializedName("business-work")
    val businessWork: com.javadEsl.pixel.data.model.topics.BusinessWork?,
    @SerializedName("color-of-water")
    val colorOfWater: com.javadEsl.pixel.data.model.topics.ColorOfWater?,
    @SerializedName("current-events")
    val currentEvents: com.javadEsl.pixel.data.model.topics.CurrentEvents?,
    @SerializedName("3d-renders")
    val dRenders: com.javadEsl.pixel.data.model.topics.DRenders?,
    @SerializedName("experimental")
    val experimental: com.javadEsl.pixel.data.model.topics.Experimental?,
    @SerializedName("fashion")
    val fashion: com.javadEsl.pixel.data.model.topics.Fashion?,
    @SerializedName("food-drink")
    val foodDrink: com.javadEsl.pixel.data.model.topics.FoodDrink?,
    @SerializedName("health")
    val health: com.javadEsl.pixel.data.model.topics.Health?,
    @SerializedName("nature")
    val nature: com.javadEsl.pixel.data.model.topics.Nature?,
    @SerializedName("people")
    val people: com.javadEsl.pixel.data.model.topics.People?,
    @SerializedName("spirituality")
    val spirituality: com.javadEsl.pixel.data.model.topics.Spirituality?,
    @SerializedName("street-photography")
    val streetPhotography: com.javadEsl.pixel.data.model.topics.StreetPhotography?,
    @SerializedName("textures-patterns")
    val texturesPatterns: com.javadEsl.pixel.data.model.topics.TexturesPatterns?,
    @SerializedName("travel")
    val travel: com.javadEsl.pixel.data.model.topics.Travel?,
    @SerializedName("wallpapers")
    val wallpapers: com.javadEsl.pixel.data.model.topics.Wallpapers?
)