package thierry.myweather.ui.cityDetailFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.myweather.model.OpenWeatherResponse
import thierry.myweather.repositories.OpenWeatherMapRepository
import javax.inject.Inject

@HiltViewModel
class CityDetailViewModel @Inject constructor(private val openWeatherMapRepository: OpenWeatherMapRepository) :
    ViewModel() {

    fun callOpenWeatherMap(cityName: String, countryName: String) {
        openWeatherMapRepository.callOpenWeatherMap(cityName, countryName)
    }

    fun getOpenWeatherResponse(): LiveData<OpenWeatherResponse> {
        return openWeatherMapRepository.getOpenWeatherResponse()
    }

}