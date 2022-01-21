package thierry.myweather.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import thierry.myweather.model.OpenWeatherResponse
import thierry.myweather.service.OpenWeatherMapService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenWeatherMapRepository @Inject constructor(private val openWeatherMapService: OpenWeatherMapService) {

    private var openWeatherResponse = MutableLiveData<OpenWeatherResponse>()
    private var isFailure = MutableLiveData<Boolean>()

    fun getOpenWeatherResponse(): LiveData<OpenWeatherResponse> {
        return openWeatherResponse
    }

    fun getIsFailure(): LiveData<Boolean> {
        return isFailure
    }

    fun callOpenWeatherMapApi(cityName: String, countryName: String) {
        val cityNameAndCountryName = "$cityName,$countryName"
        openWeatherMapService.getWeatherByCityName(cityNameAndCountryName)
            .enqueue(object : Callback<OpenWeatherResponse> {
                override fun onResponse(
                    call: Call<OpenWeatherResponse>,
                    response: Response<OpenWeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        openWeatherResponse.value = response.body()
                        isFailure.value = false
                    }
                }

                override fun onFailure(call: Call<OpenWeatherResponse>, t: Throwable) {
                    isFailure.value = true
                    Log.i("THIERRYBITAR", "FAIL")
                }
            })
    }

}