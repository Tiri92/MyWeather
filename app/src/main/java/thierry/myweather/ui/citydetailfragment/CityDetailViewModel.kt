package thierry.myweather.ui.citydetailfragment

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
    private var getIsFailure = openWeatherMapRepository.getIsFailure()
    private var getCurrentConnectionState = viewModelRepository.getCurrentConnectionState()
    private val mediatorLiveData: MediatorLiveData<CityDetailViewState> =
        MediatorLiveData<CityDetailViewState>()
    var cityName: String? = null
    var cityCountry: String? = null
    var found: OpenWeatherResponse? = null

    init {

        openWeatherMapRepository.setIsFailureToFalse()

        mediatorLiveData.addSource(getWeatherIconsUrl) { weatherIconsUrl ->
            if (weatherIconsUrl != null) {
                combine(weatherIconsUrl, null, getIsFailure.value, getCurrentConnectionState.value)
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

                combine(
                    getWeatherIconsUrl.value,
                    openWeatherResponseFromFirestore,
                    getIsFailure.value,
                    getCurrentConnectionState.value
                )
            }
        }

        mediatorLiveData.addSource(getIsFailure) { isFailure ->
            combine(
                getWeatherIconsUrl.value,
                getOpenWeatherResponseFromFirestore.value,
                isFailure,
                getCurrentConnectionState.value
            )
        }

        mediatorLiveData.addSource(getCurrentConnectionState) { isConnected ->
            combine(
                getWeatherIconsUrl.value,
                getOpenWeatherResponseFromFirestore.value,
                getIsFailure.value,
                isConnected
            )
        }

    }

    private fun combine(
        weatherIconsUrl: List<WeatherIconUrl>?,
        openWeatherResponseFromFirestore: OpenWeatherResponse?,
        isFailure: Boolean?,
        isConnected: Boolean?
    ) {
        val viewState = CityDetailViewState()
        viewState.weatherIconsUrl = weatherIconsUrl
        viewState.openWeatherResponseFromFirestore = openWeatherResponseFromFirestore
        viewState.isFailure = isFailure
        viewState.isConnected = isConnected
        mediatorLiveData.value = viewState
    }

    fun getViewState(): LiveData<CityDetailViewState> {
        return mediatorLiveData
    }

    fun callOpenWeatherMap(cityName: String, countryName: String) {
        openWeatherMapRepository.callOpenWeatherMapApi(cityName, countryName)
    }

    fun getOpenWeatherResponseListFromFirestore(): MutableList<OpenWeatherResponse> {
        return firestoreRepository.openWeatherResponseListFromFirestore
    }

}