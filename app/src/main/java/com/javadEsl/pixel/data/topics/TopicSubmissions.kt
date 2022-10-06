package com.javadEsl.pixel.data.topics


import com.google.gson.annotations.SerializedName

data class TopicSubmissions(
    @SerializedName("act-for-nature")
    val actForNature: ActForNature?,
    @SerializedName("architecture")
    val architecture: Architecture?,
    @SerializedName("arts-culture")
    val artsCulture: ArtsCulture?,
    @SerializedName("business-work")
    val businessWork: BusinessWork?,
    @SerializedName("color-of-water")
    val colorOfWater: ColorOfWater?,
    @SerializedName("current-events")
    val currentEvents: CurrentEvents?,
    @SerializedName("3d-renders")
    val dRenders: DRenders?,
    @SerializedName("experimental")
    val experimental: Experimental?,
    @SerializedName("fashion")
    val fashion: Fashion?,
    @SerializedName("food-drink")
    val foodDrink: FoodDrink?,
    @SerializedName("health")
    val health: Health?,
    @SerializedName("nature")
    val nature: Nature?,
    @SerializedName("people")
    val people: People?,
    @SerializedName("spirituality")
    val spirituality: Spirituality?,
    @SerializedName("street-photography")
    val streetPhotography: StreetPhotography?,
    @SerializedName("textures-patterns")
    val texturesPatterns: TexturesPatterns?,
    @SerializedName("travel")
    val travel: Travel?,
    @SerializedName("wallpapers")
    val wallpapers: Wallpapers?
)