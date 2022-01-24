package thierry.myweather.ui.cityDetailFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.myweather.model.OpenWeatherResponse
import thierry.myweather.model.WeatherIconUrl
import thierry.myweather.repositories.FirestoreRepository
import thierry.myweather.repositories.OpenWeatherMapRepository
import thierry.myweather.repositories.ViewModelRepository
import thierry.myweather.repositories.WeatherDatabaseRepository
import javax.inject.Inject

@HiltViewModel
class CityDetailViewModel @Inject constructor(
    weatherDatabaseRepository: WeatherDatabaseRepository,
    private val openWeatherMapRepository: OpenWeatherMapRepository,
    private val viewModelRepository: ViewModelRepository,
    private val firestoreRepository: FirestoreRepository
) :
    ViewModel() {

    private var getWeatherIconsUrl = weatherDatabaseRepository.getWeatherIconsUrl().asLiveData()
    private var getOpenWeatherResponseFromApi = openWeatherMapRepository.getOpenWeatherResponse()
    private var getOpenWeatherResponseFromFirestore =
        firestoreRepository.getOpenWeatherResponseFromFirestore()
    private val mediatorLiveData: MediatorLiveData<CityDetailViewState> =
        MediatorLiveData<CityDetailViewState>()
    var cityName: String? = null
    var cityCountry: String? = null

    init {

        mediatorLiveData.addSource(getWeatherIconsUrl) { weatherIconsUrl ->
            if (weatherIconsUrl != null) {
                combine(weatherIconsUrl, null)
            }
        }

        mediatorLiveData.addSource(getOpenWeatherResponseFromApi) { openWeatherResponseFromApi ->
            if (openWeatherResponseFromApi != null) {
                firestoreRepository.createInfoCityWeatherInFirestore(
                    openWeatherResponseFromApi,
                    "${openWeatherResponseFromApi.name}-${openWeatherResponseFromApi.sys?.country}"
                )
            }
        }

        mediatorLiveData.addSource(getOpenWeatherResponseFromFirestore) { openWeatherResponseFromFirestore ->
            if (openWeatherResponseFromFirestore != null && openWeatherResponseFromFirestore.name == cityName && openWeatherResponseFromFirestore.sys?.country == cityCountry) {

                combine(getWeatherIconsUrl.value, openWeatherResponseFromFirestore)
            }
        }

    }

    private fun combine(
        weatherIconsUrl: List<WeatherIconUrl>?,
        openWeatherResponseFromFirestore: OpenWeatherResponse?
    ) {
        val viewState = CityDetailViewState()
        viewState.weatherIconsUrl = weatherIconsUrl
        viewState.openWeatherResponseFromFirestore = openWeatherResponseFromFirestore
        mediatorLiveData.value = viewState
    }

    fun getViewState(): LiveData<CityDetailViewState> {
        return mediatorLiveData
    }

    fun callOpenWeatherMap(cityName: String, countryName: String) {
        openWeatherMapRepository.callOpenWeatherMapApi(cityName, countryName)
    }

    fun getIsFailure(): LiveData<Boolean> {
        return openWeatherMapRepository.getIsFailure()
    }

    fun getCurrentConnectionState() = viewModelRepository.getCurrentConnectionState()

    fun getOpenWeatherResponseListFromFirestore(): MutableList<OpenWeatherResponse> {
        return firestoreRepository.openWeatherResponseListFromFirestore
    }

}