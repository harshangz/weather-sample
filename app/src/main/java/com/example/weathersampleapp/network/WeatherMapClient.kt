package com.example.weathersampleapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object WeatherMapClient {
    private const val BASE_URL = "https://api.openweathermap.org"
    private var retrofit: Retrofit? = null
    val client: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
}