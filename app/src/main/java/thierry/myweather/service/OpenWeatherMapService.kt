package thierry.myweather.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import thierry.myweather.BuildConfig
import thierry.myweather.model.OpenWeatherResponse

interface OpenWeatherMapService {

    @GET("weather?appid=${BuildConfig.WEATHER_API_KEY}&units=metric&lang=fr")
    fun getWeatherByCityName(
        @Query("q") cityNameAndCountryName: String
    ): Call<OpenWeatherResponse>

}