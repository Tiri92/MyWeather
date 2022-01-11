package thierry.myweather.repositories

import thierry.myweather.database.dao.WeatherDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherDatabaseRepository @Inject constructor(private val weatherDao: WeatherDao) {
    fun getCities() = weatherDao.getCities()
}