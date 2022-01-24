package thierry.myweather.repositories

import thierry.myweather.database.dao.WeatherDao
import thierry.myweather.model.City
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherDatabaseRepository @Inject constructor(private val weatherDao: WeatherDao) {
    fun getCities() = weatherDao.getCities()

    fun getWeatherIconsUrl() = weatherDao.getWeatherIconUrl()

    suspend fun addCity(city: City): Long = weatherDao.insertCity(city)

    suspend fun updateCity(city: City) = weatherDao.updateCity(city)

    suspend fun deleteCity(city: City): Int = weatherDao.deleteCity(city)
}