package com.example.weathersampleapp.model.common

import com.google.gson.annotations.SerializedName

class Sys {
    @SerializedName("type")
    val type = 0.0

    @SerializedName("id")
    val id: Long? = null

    @SerializedName("message")
    val message: Double? = null

    @SerializedName("country")
    val country: String? = null

    @SerializedName("sunrise")
    val sunrise: Long? = null

    @SerializedName("sunset")
    val sunset: Long? = null

    @SerializedName("pod")
    val pod: Char? = null
}