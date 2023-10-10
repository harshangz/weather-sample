package com.example.weathersampleapp.model.common

import com.google.gson.annotations.SerializedName

class Main {
    @SerializedName("temp")
    val temp = 0.0

    @SerializedName("feels_like")
    val feelsLike = 0.0

    @SerializedName("temp_min")
    val tempMin = 0.0

    @SerializedName("temp_max")
    val tempMax = 0.0

    @SerializedName("pressure")
    val pressure = 0.0

    @SerializedName("humidity")
    val humidity = 0.0

    @SerializedName("sea_level")
    val seaLevel: Double? = null

    @SerializedName("grnd_level")
    val grndLevel: Double? = null

    @SerializedName("temp_kf")
    val tempKf: Double? = null
}