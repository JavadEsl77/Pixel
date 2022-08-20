package com.javadEsl.imageSearchApp.data


import com.google.gson.annotations.SerializedName

data class Location(
    val city: String?,
    val country: String?,
    val name: String?,
    val position: Position?
)