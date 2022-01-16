package thierry.myweather.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.myweather.repositories.ViewModelRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val viewModelRepository: ViewModelRepository) :
    ViewModel() {

    private var isConnectedToInternet: Boolean? = null

    fun getCurrentConnectionState(connectionState: Boolean) {
        isConnectedToInternet = connectionState
    }

    fun setCurrentConnectionState() = viewModelRepository.setCurrentConnectionState(
        isConnectedToInternet!!
    )

}