package com.example.weathersampleapp.network

import com.example.weathersampleapp.model.CurrentWeather
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection

interface CurrentWeatherCallback {
    fun onSuccess(currentWeather: CurrentWeather?)
    fun onFailure(throwable: Throwable?)
}

class RemoteRepo (var apiKey: String?){
    private val APPID = "appId"
    private val UNITS = "units"
    private val LANGUAGE = "lang"
    private val QUERY = "q"
    private val LATITUDE = "lat"
    private val LONGITUDE = "lon"

    private var weatherMapService: WeatherMapService? = null
    private var options: MutableMap<String, String>? = null

    init {
        weatherMapService =
            WeatherMapClient.client?.create(WeatherMapService::class.java)
        options = HashMap()
        (options as HashMap<String, String>)[APPID] = apiKey ?: ""
    }

    //SETUP METHODS
    fun setUnits(units: String) {
        options!![UNITS] = units
    }

    fun setLanguage(lang: String) {
        options!![LANGUAGE] = lang
    }


    private fun NoAppIdErrMessage(): Throwable {
        return Throwable("UnAuthorized. Please set a valid OpenWeatherMap API KEY.")
    }


    private fun NotFoundErrMsg(str: String): Throwable {
        var throwable: Throwable? = null
        try {
            val obj = JSONObject(str)
            throwable = Throwable(obj.getString("message"))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (throwable == null) {
            throwable = Throwable("An unexpected error occurred.")
        }
        return throwable
    }

    //GET CURRENT WEATHER BY CITY NAME
    fun getCurrentWeatherByCityName(city: String, callback: CurrentWeatherCallback) {
        options!![QUERY] = city
        weatherMapService!!.getCurrentWeatherByCityName(options)
            ?.enqueue(object : Callback<CurrentWeather?> {

                override fun onFailure(call: Call<CurrentWeather?>, throwable: Throwable) {
                    callback.onFailure(throwable)
                }

                override fun onResponse(
                    call: Call<CurrentWeather?>,
                    response: Response<CurrentWeather?>
                ) {
                    handleCurrentWeatherResponse(response, callback)
                }
            })
    }

    //GET CURRENT WEATHER BY GEOGRAPHIC COORDINATES
    fun getCurrentWeatherByGeoCoordinates(
        latitude: Double,
        longitude: Double,
        callback: CurrentWeatherCallback
    ) {
        options!![LATITUDE] = latitude.toString()
        options!![LONGITUDE] = longitude.toString()
        weatherMapService!!.getCurrentWeatherByGeoCoordinates(options)
            ?.enqueue(object : Callback<CurrentWeather?> {
                override fun onResponse(
                    call: Call<CurrentWeather?>,
                    response: Response<CurrentWeather?>
                ) {
                    handleCurrentWeatherResponse(response, callback)
                }

                override fun onFailure(call: Call<CurrentWeather?>, throwable: Throwable) {
                    callback.onFailure(throwable)
                }
            })
    }

    private fun handleCurrentWeatherResponse(
        response: Response<CurrentWeather?>,
        callback: CurrentWeatherCallback
    ) {
        if (response.code() === HttpURLConnection.HTTP_OK) {
            callback.onSuccess(response.body())
        } else if (response.code() === HttpURLConnection.HTTP_FORBIDDEN || response.code() === HttpURLConnection.HTTP_UNAUTHORIZED) {
            callback.onFailure(NoAppIdErrMessage())
        } else {
            try {
                callback.onFailure(NotFoundErrMsg(response.errorBody()!!.string()))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}