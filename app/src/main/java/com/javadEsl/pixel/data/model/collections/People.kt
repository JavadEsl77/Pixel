package com.javadEsl.pixel.data.model.collections


import com.google.gson.annotations.SerializedName

data class People(
    @SerializedName("approved_on")
    val approvedOn: String?,
    val status: String?
)