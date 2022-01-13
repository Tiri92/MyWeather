package thierry.myweather.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import thierry.myweather.model.City

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
}