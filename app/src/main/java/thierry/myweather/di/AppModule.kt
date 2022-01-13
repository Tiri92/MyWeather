package thierry.myweather.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import thierry.myweather.database.WeatherDatabase
import thierry.myweather.service.OpenWeatherMapService
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext app: Context,
        callback: WeatherDatabase.Callback,
    ) = Room.databaseBuilder(app, WeatherDatabase::class.java, "weather_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    @Singleton
    fun provideWeatherDao(db: WeatherDatabase) = db.dao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun provideRetrofitInstance(): OpenWeatherMapService = Retrofit.Builder()
        .baseUrl("http://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenWeatherMapService::class.java)
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope