package thierry.myweather.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import thierry.myweather.di.ApplicationScope
import thierry.myweather.model.City
import thierry.myweather.database.dao.WeatherDao
import thierry.myweather.model.WeatherIconUrl
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [City::class, WeatherIconUrl::class],
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

                dao.insertCity(City(name = "Paris", countryCode = "FR"))
                dao.insertCity(City(name = "Rennes", countryCode = "FR"))
                dao.insertCity(City(name = "Toulouse", countryCode = "FR"))

                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "01d",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F01d.png?alt=media&token=94f85c92-d69f-487f-957d-da927142f85d"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "01n",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F01n.png?alt=media&token=f60b65bd-7197-46ea-8878-227a035cbe8f"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "02d",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F02d.png?alt=media&token=611a3c84-4a49-40b8-aaa1-4ab41c9cbc7e"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "02n",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F02n.png?alt=media&token=0ca9040a-c18a-44b9-8630-5ab5f142465d"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "03d",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F03d.png?alt=media&token=c8cb97e0-5562-4660-af26-2a9584f50cf4"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "03n",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F03n.png?alt=media&token=e339edcc-44d4-4a29-9fcc-c65da82212aa"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "04d",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F04d.png?alt=media&token=edd39d97-5c14-4eef-b287-80cec1446b41"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "04n",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F04n.png?alt=media&token=57baf2e4-6997-4f18-8329-13658e3905ce"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "09d",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F09d.png?alt=media&token=ba7871ac-1d47-46b5-ba00-c6250420af64"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "09n",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F09n.png?alt=media&token=a4e884eb-43e0-4ad6-9215-bc4ee68e00e9"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "10d",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F10d.png?alt=media&token=12c35576-4b05-49da-b086-08c698e5fcb5"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "10n",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F10n.png?alt=media&token=20650b49-415c-4bff-b639-86e21629e3ba"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "11d",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F11d.png?alt=media&token=eea3a052-f8a1-4773-9e61-6d806bf1c589"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "11n",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F11n.png?alt=media&token=1cd5f3c3-ad53-4015-b7fe-5c5080dd00f8"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "13d",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F13d.png?alt=media&token=1e5c71d7-4eae-4294-aafd-1ec83fb90d90"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "13n",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F13n.png?alt=media&token=8330c5e7-c415-414d-be71-fc37730f2be8"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "50d",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F50d.png?alt=media&token=3eed1ce8-2692-4cc3-b5da-2a2809ea9adb"
                    )
                )
                dao.insertWeatherIconUrl(
                    WeatherIconUrl(
                        name = "50n",
                        firestoreStorageUrl = "https://firebasestorage.googleapis.com/v0/b/myweather-27bc1.appspot.com/o/WeatherImage%2F50n.png?alt=media&token=2e66d8d8-a1ff-4ecf-b97f-04fd84b49fbf"
                    )
                )

            }
        }
    }

}