package com.javadEsl.pixel.data.model.autocomplete


import com.google.gson.annotations.SerializedName

data class Suggestion(
    val priority: Int?,
    val query: String?
)