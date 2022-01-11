package thierry.myweather.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.myweather.repositories.WeatherDatabaseRepository
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val weatherDatabaseRepository: WeatherDatabaseRepository) :
    ViewModel() {
    fun getCities() = weatherDatabaseRepository.getCities().asLiveData()
}