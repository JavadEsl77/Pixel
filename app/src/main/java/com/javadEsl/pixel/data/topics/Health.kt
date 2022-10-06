package com.javadEsl.pixel.data.topics


import com.google.gson.annotations.SerializedName

data class Health(
    @SerializedName("status")
    val status: String
)