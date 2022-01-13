package thierry.myweather.ui.citiesfragment

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import thierry.myweather.model.City
import thierry.myweather.repositories.WeatherDatabaseRepository
import javax.inject.Inject

@HiltViewModel
class CitiesViewModel @Inject constructor(private val weatherDatabaseRepository: WeatherDatabaseRepository) :
    ViewModel() {

    fun getCities() = weatherDatabaseRepository.getCities().asLiveData()

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

}