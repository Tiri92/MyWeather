package thierry.myweather.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import thierry.myweather.di.ApplicationScope
import thierry.myweather.model.City
import thierry.myweather.database.dao.WeatherDao
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [City::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun dao(): WeatherDao

    class Callback @Inject constructor(
        private val database: Provider<WeatherDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope,
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val dao = database.get().dao()

            applicationScope.launch {

                dao.insertCity(City(name = "Paris"))
                dao.insertCity(City(name = "Rennes"))
                dao.insertCity(City(name = "Toulouse"))

            }
        }
    }

}