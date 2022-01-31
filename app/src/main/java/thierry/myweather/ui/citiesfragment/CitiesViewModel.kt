package thierry.myweather.ui.citiesfragment

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import thierry.myweather.model.City
import thierry.myweather.model.OpenWeatherResponse
import thierry.myweather.model.WeatherIconUrl
import thierry.myweather.repositories.FirestoreRepository
import thierry.myweather.repositories.OpenWeatherMapRepository
import thierry.myweather.repositories.ViewModelRepository
import thierry.myweather.repositories.WeatherDatabaseRepository
import javax.inject.Inject

@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val weatherDatabaseRepository: WeatherDatabaseRepository,
    private val openWeatherMapRepository: OpenWeatherMapRepository,
    private val viewModelRepository: ViewModelRepository,
    private val firestoreRepository: FirestoreRepository
) :
    ViewModel() {

    private val mediatorLiveData: MediatorLiveData<CitiesViewState> =
        MediatorLiveData<CitiesViewState>()
    private var getCitiesFromRoom = weatherDatabaseRepository.getCities().asLiveData()
    private var getWeatherIconsUrl = weatherDatabaseRepository.getWeatherIconsUrl().asLiveData()
    private var getCitiesFromFirestore = firestoreRepository.getCitiesFromFirestore()
    private var getOpenWeatherResponseFromApi = openWeatherMapRepository.getOpenWeatherResponse()
    private var getOpenWeatherResponseFromFirestore =
        firestoreRepository.getOpenWeatherResponseFromFirestore()
    private var getCurrentConnectionState = viewModelRepository.getCurrentConnectionState()

    init {

        firestoreRepository.openWeatherResponseListFromFirestore.clear()

        mediatorLiveData.addSource(getCitiesFromRoom) { citiesListFromRoom ->
            if (citiesListFromRoom != null) {
                citiesListFromRoom.forEach { city ->
                    firestoreRepository.callOpenWeatherResponseFirestoreRequest("${city.name}-${city.countryCode}")
                }
                firestoreRepository.callCitiesFromFirestore()
                combine(
                    citiesListFromRoom,
                    getWeatherIconsUrl.value,
                    getCitiesFromFirestore.value,
                    getOpenWeatherResponseFromApi.value,
                    firestoreRepository.openWeatherResponseListFromFirestore,
                    getCurrentConnectionState.value
                )
            }
        }

        mediatorLiveData.addSource(getWeatherIconsUrl) { weatherIconsUrl ->
            if (!weatherIconsUrl.isNullOrEmpty()) {
                combine(
                    getCitiesFromRoom.value,
                    weatherIconsUrl,
                    getCitiesFromFirestore.value,
                    getOpenWeatherResponseFromApi.value,
                    firestoreRepository.openWeatherResponseListFromFirestore,
                    getCurrentConnectionState.value
                )
            }
        }

        mediatorLiveData.addSource(getCitiesFromFirestore) { citiesListFromFirestore ->
            if (!citiesListFromFirestore.isNullOrEmpty()) {
                combine(
                    getCitiesFromRoom.value,
                    getWeatherIconsUrl.value,
                    citiesListFromFirestore,
                    getOpenWeatherResponseFromApi.value,
                    firestoreRepository.openWeatherResponseListFromFirestore,
                    getCurrentConnectionState.value
                )
            } else {
                getCitiesFromRoom.value?.forEach { city ->
                    firestoreRepository.createCityInFirestore(city)
                    openWeatherMapRepository.callOpenWeatherMapApi(city.name!!, city.countryCode!!)
                }
            }
        }

        mediatorLiveData.addSource(getOpenWeatherResponseFromApi) { openWeatherResponseFromApi ->
            if (openWeatherResponseFromApi != null) {
                var cityFound = false
                getCitiesFromFirestore.value?.forEach { city ->
                    if (city.name == openWeatherResponseFromApi.name && city.countryCode == openWeatherResponseFromApi.sys?.country) {
                        cityFound = true
                    }
                }
                if (!cityFound) {
                    firestoreRepository.createCityInFirestore(
                        City(
                            name = openWeatherResponseFromApi.name,
                            countryCode = openWeatherResponseFromApi.sys?.country
                        )
                    )
                }
                firestoreRepository.createInfoCityWeatherInFirestore(
                    openWeatherResponseFromApi,
                    "${openWeatherResponseFromApi.name}-${openWeatherResponseFromApi.sys?.country}"
                )
                combine(
                    getCitiesFromRoom.value,
                    getWeatherIconsUrl.value,
                    getCitiesFromFirestore.value,
                    openWeatherResponseFromApi,
                    firestoreRepository.openWeatherResponseListFromFirestore,
                    getCurrentConnectionState.value
                )
            }
        }

        mediatorLiveData.addSource(getOpenWeatherResponseFromFirestore) { openWeatherResponse ->
            if (openWeatherResponse != null) {
                firestoreRepository.openWeatherResponseListFromFirestore.add(openWeatherResponse)
                Log.i(
                    "THIERRYBITAR",
                    "${firestoreRepository.openWeatherResponseListFromFirestore.size}"
                )
                combine(
                    getCitiesFromRoom.value,
                    getWeatherIconsUrl.value,
                    getCitiesFromFirestore.value,
                    getOpenWeatherResponseFromApi.value,
                    firestoreRepository.openWeatherResponseListFromFirestore,
                    getCurrentConnectionState.value
                )
            }
        }

        mediatorLiveData.addSource(getCurrentConnectionState) { isConnected ->
            combine(
                getCitiesFromRoom.value,
                getWeatherIconsUrl.value,
                getCitiesFromFirestore.value,
                getOpenWeatherResponseFromApi.value,
                firestoreRepository.openWeatherResponseListFromFirestore,
                isConnected
            )
        }

    }

    private fun combine(
        citiesListFromRoom: List<City>?,
        weatherIconsUrl: List<WeatherIconUrl>?,
        citiesListFromFirestore: List<City>?,
        openWeatherResponseFromApi: OpenWeatherResponse?,
        openWeatherResponseListFromFirestore: List<OpenWeatherResponse>?,
        isConnected: Boolean?
    ) {
        val viewState = CitiesViewState()
        viewState.citiesList = citiesListFromRoom
        viewState.openWeatherResponseList = openWeatherResponseListFromFirestore
        viewState.weatherIconsUrl = weatherIconsUrl
        viewState.isConnected = isConnected
        mediatorLiveData.value = viewState
    }

    fun getViewState(): LiveData<CitiesViewState> {
        return mediatorLiveData
    }

    fun addCity(city: City) = viewModelScope.launch {
        cityIsSuccessfullyInserted.value = weatherDatabaseRepository.addCity(city)
    }

    fun updateCity(city: City) = viewModelScope.launch {
        weatherDatabaseRepository.updateCity(city)
    }

    private var cityIsSuccessfullyInserted = MutableLiveData<Long>()
    fun cityIsSuccessfullyInserted(): LiveData<Long> {
        return cityIsSuccessfullyInserted
    }

    fun deleteCity(city: City) = viewModelScope.launch {
        weatherDatabaseRepository.deleteCity(city)
    }

    fun callOpenWeatherMap(cityName: String, countryName: String) {
        openWeatherMapRepository.callOpenWeatherMapApi(cityName, countryName)
    }

    fun clearOpenWeatherResponseListFromFirestore() {
        firestoreRepository.openWeatherResponseListFromFirestore.clear()
    }

}