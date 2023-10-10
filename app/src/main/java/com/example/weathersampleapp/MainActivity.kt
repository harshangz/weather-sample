package com.example.weathersampleapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.text.format.DateFormat
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.weathersampleapp.databinding.ActivityMainBinding
import com.example.weathersampleapp.model.CurrentWeather
import com.example.weathersampleapp.network.CurrentWeatherCallback
import com.example.weathersampleapp.network.RemoteRepo
import java.util.Calendar
import java.util.Locale


class MainActivity : AppCompatActivity(),LocationListener {
    val TAG = MainActivity::class.java.simpleName
    private var permissionArrays = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private var lat: Double? = 0.0
    private var lng: Double? = 0.0
    private var helper: RemoteRepo? = null
    private lateinit var binding: ActivityMainBinding
    private var mProgressBar: ProgressDialog? = null

    fun initApiClient() {
        helper = RemoteRepo(getString(R.string.OPEN_WEATHER_MAP_API_KEY))
        helper!!.setUnits("imperial")
        helper!!.setLanguage("en")
    }

    fun initLoading() {
        mProgressBar = ProgressDialog(this)
        mProgressBar?.setTitle("Please wait")
        mProgressBar?.setCancelable(false)
        mProgressBar?.setMessage("Fetching location data...")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val MyVersion = Build.VERSION.SDK_INT
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            requestPermissions(permissionArrays, 101)
        }

        initApiClient()
        initLoading()

        binding.toolbarLayout.tvLocationName.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Please enter you city")

            val input = EditText(this)

            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            builder.setPositiveButton("Enter"
            ) { _, _ ->
                val edtCity = input.text.toString()
                getCityWeather(edtCity)
            }
            builder.setNegativeButton("Cancel"
            ) { dialog, _ -> dialog.cancel() }

            builder.show()
        }

    }

    private fun getCityWeather(name:String) {
        mProgressBar?.show()
        helper!!.getCurrentWeatherByCityName(name, object : CurrentWeatherCallback {
            override fun onSuccess(currentWeather: CurrentWeather?) {
                mProgressBar?.dismiss()
                Log.v(
                    TAG,
                    "Coordinates: " + currentWeather!!.coord!!.lat + ", " + currentWeather!!.coord!!.lon + "\n"
                            + "Weather Description: " + currentWeather!!.weather!![0].description + "\n"
                            + "Temperature: " + currentWeather.main!!.tempMax + "\n"
                            + "Wind Speed: " + currentWeather.wind!!.speed + "\n"
                            + "City, Country: " + currentWeather.name + ", " + currentWeather.sys!!.country
                )
                setWeatherData(currentWeather)
            }

            override fun onFailure(throwable: Throwable?) {
                mProgressBar?.dismiss()
                Log.v(TAG, (throwable!!.message)!!)
                Toast.makeText(baseContext, throwable.message,Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                val intent = intent
                finish()
                startActivity(intent)
            } else {
                getLatlong()
            }
        }
    }

    private fun getLatlong() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                115
            )
            return
        }
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val provider = locationManager.getBestProvider(criteria, true)
        val location = provider?.let { locationManager.getLastKnownLocation(it) }
        if (location != null) {
            onLocationChanged(location)
        } else {
            if (provider != null) {
                locationManager.requestLocationUpdates(provider, 20000, 0f, this)
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        lng = location.longitude
        lat = location.latitude

        getCurrentTime()
        getCurrentWeather()
    }

    private fun getCurrentTime() {
        val dateNow = Calendar.getInstance().time
        val currTime = DateFormat.format("EEE", dateNow) as String

        val date = Calendar.getInstance().time
        val format = DateFormat.format("d MMM yyyy", date) as String
        val formatDate = "$currTime, $format"
        binding.tvDate.text = formatDate
    }

    private fun getCurrentWeather() {
        lat?.let {
            lng?.let { it1 ->
                mProgressBar?.show()

                helper!!.getCurrentWeatherByGeoCoordinates(
                    it,
                    it1,
                    object : CurrentWeatherCallback {
                        override fun onSuccess(currentWeather: CurrentWeather?) {
                            mProgressBar?.dismiss()

                            Log.v(
                                TAG,
                                """
                            Coordinates: ${currentWeather!!.coord!!.lat}, ${currentWeather.coord!!.lon}
                            Weather Description: ${currentWeather.weather!![0].description}
                            Temperature: ${currentWeather.main!!.tempMax}
                            Wind Speed: ${currentWeather.wind!!.speed}
                            Date: ${currentWeather.dt}
                            Timezone: ${currentWeather.timezone}
                            City, Country: ${currentWeather.name}, ${currentWeather.sys!!.country}
                            """.trimIndent()
                            )
                            setWeatherData(currentWeather)

                        }

                        override fun onFailure(throwable: Throwable?) {
                            mProgressBar?.dismiss()
                            Log.v(TAG, throwable!!.message!!)
                        }
                    })
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setWeatherData(currentWeather: CurrentWeather) {
        try {
            val jsonArrayOne = currentWeather.weather
            val weatherObj = jsonArrayOne?.get(0)
            val weatherMainObj = currentWeather.main
            val windObj = currentWeather.wind
            val strWeather = weatherObj?.main
            val strDescWeather = weatherObj?.description
            val strWindSpeed = windObj?.speed
            val strHumidity = weatherMainObj?.humidity
            val strName = currentWeather.name
            val weatherTemp = weatherMainObj?.temp

            if (strDescWeather == "broken clouds") {
                binding.iconTemp.setAnimation(R.raw.broken_clouds)
                binding.tvWeather.text = "Scattered Clouds"
            } else if (strDescWeather == "light rain") {
                binding.iconTemp.setAnimation(R.raw.light_rain)
                binding.tvWeather.text = "Drizzling"
            } else if (strDescWeather == "haze") {
                binding.iconTemp.setAnimation(R.raw.broken_clouds)
                binding.tvWeather.text = "Foggy"
            } else if (strDescWeather == "overcast clouds") {
                binding.iconTemp.setAnimation(R.raw.overcast_clouds)
                binding.tvWeather.text = "Cloudy"
            } else if (strDescWeather == "moderate rain") {
                binding.iconTemp.setAnimation(R.raw.moderate_rain)
                binding.tvWeather.text = "Light rain"
            } else if (strDescWeather == "few clouds") {
                binding.iconTemp.setAnimation(R.raw.few_clouds)
                binding.tvWeather.text = "Cloudy"
            } else if (strDescWeather == "heavy intensity rain") {
                binding.iconTemp.setAnimation(R.raw.heavy_intentsity)
                binding.tvWeather.text = "Heavy rain"
            } else if (strDescWeather == "clear sky") {
                binding.iconTemp.setAnimation(R.raw.clear_sky)
                binding.tvWeather.text = "Sunny"
            } else if (strDescWeather == "scattered clouds") {
                binding.iconTemp.setAnimation(R.raw.scattered_clouds)
                binding.tvWeather.text = "Scattered Clouds"
            } else {
                binding.iconTemp.setAnimation(R.raw.unknown)
                binding.tvWeather.text = strWeather
            }

            binding.toolbarLayout.tvLocationName.text = strName
            binding.tvTemp.text =
                String.format(Locale.getDefault(), "%.0f°C", weatherTemp)
            binding.tvWind.text = "Wind velocity $strWindSpeed km/j"
            binding.tvHumidLevel.text = "Humidity $strHumidity %"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}