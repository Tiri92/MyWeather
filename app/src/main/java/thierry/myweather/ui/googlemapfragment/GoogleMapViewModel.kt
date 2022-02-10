package thierry.myweather.ui.googlemapfragment

import android.location.Location
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import thierry.myweather.repositories.FirestoreRepository
import thierry.myweather.repositories.ViewModelRepository
import javax.inject.Inject

@HiltViewModel
class GoogleMapViewModel @Inject constructor(
    viewModelRepository: ViewModelRepository,
    firestoreRepository: FirestoreRepository
) :
    ViewModel() {

    val getCurrentConnectionState = viewModelRepository.getCurrentConnectionState()

    val getOpenWeatherResponseListFromFirestore =
        firestoreRepository.openWeatherResponseListFromFirestore

    /** User position  **/
    var currentPosition: LatLng? = null

    fun setLocationInLatLng(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }
    /** **/

}