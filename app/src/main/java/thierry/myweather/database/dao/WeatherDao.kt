package thierry.myweather.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import thierry.myweather.model.City

@Dao
interface WeatherDao {
    @Query("SELECT * FROM city_table")
    fun getCities(): Flow<List<City>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: City): Long
}