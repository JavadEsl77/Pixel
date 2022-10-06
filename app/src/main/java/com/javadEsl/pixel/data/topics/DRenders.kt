package com.javadEsl.pixel.data.topics


import com.google.gson.annotations.SerializedName

data class DRenders(
    @SerializedName("approved_on")
    val approvedOn: String,
    @SerializedName("status")
    val status: String
)