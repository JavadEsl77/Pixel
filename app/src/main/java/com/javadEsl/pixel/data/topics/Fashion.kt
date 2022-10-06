package com.javadEsl.pixel.data.topics


import com.google.gson.annotations.SerializedName

data class Fashion(
    @SerializedName("approved_on")
    val approvedOn: String?,
    @SerializedName("status")
    val status: String
)