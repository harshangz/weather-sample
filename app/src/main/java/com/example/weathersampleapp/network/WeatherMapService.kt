package com.example.weathersampleapp.network

import com.example.weathersampleapp.model.CurrentWeather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap


interface WeatherMapService {
    //Current Weather Endpoints start
    @GET(CURRENT)
    fun getCurrentWeatherByCityName(@QueryMap options: MutableMap<String, String>?): Call<CurrentWeather?>?

    @GET(CURRENT)
    fun getCurrentWeatherByGeoCoordinates(@QueryMap options: MutableMap<String, String>?): Call<CurrentWeather?>?

    companion object {
        const val CURRENT = "/data/2.5/weather"
        const val FORECAST = "/data/2.5/forecast"
    }
}