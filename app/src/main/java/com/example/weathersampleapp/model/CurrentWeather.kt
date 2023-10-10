package com.example.weathersampleapp.model

import com.example.weathersampleapp.model.common.Clouds
import com.example.weathersampleapp.model.common.Coord
import com.example.weathersampleapp.model.common.Main
import com.example.weathersampleapp.model.common.Rain
import com.example.weathersampleapp.model.common.Snow
import com.example.weathersampleapp.model.common.Sys
import com.example.weathersampleapp.model.common.Weather
import com.example.weathersampleapp.model.common.Wind
import com.google.gson.annotations.SerializedName

class CurrentWeather {
    @SerializedName("coord")
    val coord: Coord? = null

    @SerializedName("weather")
    val weather: List<Weather>? = null

    @SerializedName("base")
    val base: String? = null

    @SerializedName("main")
    val main: Main? = null

    @SerializedName("visibility")
    val visibility: Long? = null

    @SerializedName("wind")
    val wind: Wind? = null

    @SerializedName("clouds")
    val clouds: Clouds? = null

    @SerializedName("rain")
    val rain: Rain? = null

    @SerializedName("snow")
    val snow: Snow? = null

    @SerializedName("dt")
    val dt: Long? = null

    @SerializedName("sys")
    val sys: Sys? = null

    @SerializedName("timezone")
    val timezone: Long? = null

    @SerializedName("id")
    val id: Long? = null

    @SerializedName("name")
    val name: String? = null

    @SerializedName("cod")
    val cod: Int? = null
}