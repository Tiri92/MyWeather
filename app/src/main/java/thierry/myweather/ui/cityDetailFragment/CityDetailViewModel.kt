package thierry.myweather.ui.cityDetailFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.myweather.model.OpenWeatherResponse
import thierry.myweather.repositories.OpenWeatherMapRepository
import thierry.myweather.repositories.ViewModelRepository
import javax.inject.Inject

@HiltViewModel
class CityDetailViewModel @Inject constructor(
    private val openWeatherMapRepository: OpenWeatherMapRepository,
    private val viewModelRepository: ViewModelRepository
) :
    ViewModel() {

    fun callOpenWeatherMap(cityName: String, countryName: String) {
        openWeatherMapRepository.callOpenWeatherMap(cityName, countryName)
    }

    fun getOpenWeatherResponse(): LiveData<OpenWeatherResponse> {
        return openWeatherMapRepository.getOpenWeatherResponse()
    }

    fun getIsFailure(): LiveData<Boolean> {
        return openWeatherMapRepository.getIsFailure()
    }

    fun getCurrentConnectionState() = viewModelRepository.getCurrentConnectionState()

}