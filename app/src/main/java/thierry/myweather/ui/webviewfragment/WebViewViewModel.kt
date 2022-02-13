package thierry.myweather.ui.webviewfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.myweather.repositories.WeatherDatabaseRepository
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel @Inject constructor(
    weatherDatabaseRepository: WeatherDatabaseRepository
) :
    ViewModel() {

    val getCitiesFromRoom = weatherDatabaseRepository.getCities().asLiveData()

}