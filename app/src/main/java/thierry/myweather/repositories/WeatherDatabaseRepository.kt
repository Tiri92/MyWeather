package thierry.myweather.repositories

import thierry.myweather.database.dao.WeatherDao
import thierry.myweather.model.City
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherDatabaseRepository @Inject constructor(private val weatherDao: WeatherDao) {
    fun getCities() = weatherDao.getCities()

    fun getNewIdCityTable() = weatherDao.getNewIdCityTable()

    suspend fun addCity(city: City): Long = weatherDao.insertCity(city)
}