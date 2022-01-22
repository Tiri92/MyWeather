package thierry.myweather.ui.citiesfragment

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import thierry.myweather.model.City
import thierry.myweather.model.OpenWeatherResponse
import thierry.myweather.repositories.FirestoreRepository
import thierry.myweather.repositories.OpenWeatherMapRepository
import thierry.myweather.repositories.WeatherDatabaseRepository
import javax.inject.Inject

@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val weatherDatabaseRepository: WeatherDatabaseRepository,
    private val openWeatherMapRepository: OpenWeatherMapRepository,
    private val firestoreRepository: FirestoreRepository
) :
    ViewModel() {

    private val mediatorLiveData: MediatorLiveData<CitiesViewState> =
        MediatorLiveData<CitiesViewState>()
    private var getCitiesFromRoom = weatherDatabaseRepository.getCities().asLiveData()
    private var getCitiesFromFirestore = firestoreRepository.getCitiesFromFirestore()
    private var getOpenWeatherResponseFromApi = openWeatherMapRepository.getOpenWeatherResponse()
    private var getOpenWeatherResponseFromFirestore =
        firestoreRepository.getOpenWeatherResponseFromFirestore()

    init {

        mediatorLiveData.addSource(getCitiesFromRoom) { citiesListFromRoom ->
            if (citiesListFromRoom != null) {
                citiesListFromRoom.forEach { city ->
                    firestoreRepository.callOpenWeatherResponseFirestoreRequest("${city.name}-${city.countryCode}")
                }
                firestoreRepository.callCitiesFromFirestore()
                combine(
                    citiesListFromRoom,
                    getCitiesFromFirestore.value,
                    getOpenWeatherResponseFromApi.value,
                    firestoreRepository.openWeatherResponseListFromFirestore
                )
            }
        }

        mediatorLiveData.addSource(getCitiesFromFirestore) { citiesListFromFirestore ->
            if (!citiesListFromFirestore.isNullOrEmpty()) {
                combine(
                    getCitiesFromRoom.value,
                    citiesListFromFirestore,
                    getOpenWeatherResponseFromApi.value,
                    firestoreRepository.openWeatherResponseListFromFirestore
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
                firestoreRepository.createInfoCityWeatherInFirestore(
                    openWeatherResponseFromApi,
                    "${openWeatherResponseFromApi.name}-${openWeatherResponseFromApi.sys?.country}"
                )
                firestoreRepository.openWeatherResponseListFromFirestore.add(
                    openWeatherResponseFromApi
                )
                combine(
                    getCitiesFromRoom.value,
                    getCitiesFromFirestore.value,
                    openWeatherResponseFromApi,
                    firestoreRepository.openWeatherResponseListFromFirestore
                )
            }
        }

        mediatorLiveData.addSource(getOpenWeatherResponseFromFirestore) { openWeatherResponse ->
            if (openWeatherResponse != null) {
                firestoreRepository.openWeatherResponseListFromFirestore.add(openWeatherResponse)
                combine(
                    getCitiesFromRoom.value,
                    getCitiesFromFirestore.value,
                    getOpenWeatherResponseFromApi.value,
                    firestoreRepository.openWeatherResponseListFromFirestore
                )
            } // Je passe pas dans le Else quand y a pas de result alors qu'on a lancé requete firestore, exemple si 5 requete et que la derniere renvoie rien, j'passe pas dans le else, Button ?
        }

    }

    private fun combine(
        citiesListFromRoom: List<City>?,
        citiesListFromFirestore: List<City>?,
        openWeatherResponseFromApi: OpenWeatherResponse?,
        openWeatherResponseListFromFirestore: List<OpenWeatherResponse>?
    ) {
        val viewState = CitiesViewState()
        viewState.citiesList = citiesListFromRoom

//        if (!citiesListFromRoom.isNullOrEmpty() && citiesListFromRoom.size != citiesListFromFirestore?.size) {
//            citiesListFromRoom.forEach { city ->
//                val finded =
//                    openWeatherResponseListFromFirestore?.find { predicate -> city.name == predicate.name }
//                if (finded == null) {
//                    openWeatherMapRepository.callOpenWeatherMapApi(city.name!!, city.countryCode!!)
//                }
//            }
//        }

        viewState.openWeatherResponseList = openWeatherResponseListFromFirestore
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

    fun createCityInFirestore(city: City) {
        firestoreRepository.createCityInFirestore(city)
    }

    fun createInfoCityWeatherInFirestore(
        citiesListFromRoom: List<City>,
        openWeatherResponseFromApi: OpenWeatherResponse
    ) {
        citiesListFromRoom.forEach { city ->
            firestoreRepository.createInfoCityWeatherInFirestore(
                openWeatherResponseFromApi,
                "${city.name}-${city.countryCode}"
            )
        }
    }

}