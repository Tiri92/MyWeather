package thierry.myweather.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelRepository @Inject constructor() {

    private var currentConnectionState = MutableLiveData<Boolean>()

    fun setCurrentConnectionState(connectionState: Boolean) {
        currentConnectionState.value = connectionState
    }

    fun getCurrentConnectionState(): LiveData<Boolean> {
        return currentConnectionState
    }

}