package thierry.myweather.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import thierry.myweather.model.City
import thierry.myweather.model.WeatherIconUrl

@Dao
interface WeatherDao {
    @Query("SELECT * FROM city_table")
    fun getCities(): Flow<List<City>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: City): Long

    @Update
    suspend fun updateCity(city: City)

    @Delete
    suspend fun deleteCity(city: City): Int

    @Query("SELECT * FROM weather_icon_url_table")
    fun getWeatherIconUrl(): Flow<List<WeatherIconUrl>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherIconUrl(weatherIconUrl: WeatherIconUrl)
}