package thierry.myweather.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import thierry.myweather.model.City
import thierry.myweather.model.OpenWeatherResponse
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private const val COLLECTION_WEATHER = "weather"
private const val COLLECTION_WEATHER_INFO = "weather-info"

@Singleton
class FirestoreRepository @Inject constructor() {

    private val mutableOpenWeatherResponseFromFirestore: MutableLiveData<OpenWeatherResponse> =
        MutableLiveData<OpenWeatherResponse>()

    // Get the Collection Reference
    private fun getWeatherCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_WEATHER)
    }

    private fun getWeatherInfoCollection(cityAndCountryName: String): CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_WEATHER)
            .document(cityAndCountryName)
            .collection(
                COLLECTION_WEATHER_INFO
            )
    }

    fun createCityInFirestore(city: City) {
        getWeatherCollection().document(city.name.toString() + "-" + city.countryCode).set(city)
            .addOnSuccessListener { Log.i("THIERRYBITAR", "SUCCESS") }
            .addOnFailureListener { Log.i("THIERRYBITAR", "FAIL") }
    }

    fun createInfoCityWeatherInFirestore(
        openWeatherResponse: OpenWeatherResponse,
        cityAndCountryName: String
    ) {
        getWeatherInfoCollection(cityAndCountryName).document(cityAndCountryName)
            .set(openWeatherResponse)
    }

    // Get All Cities from Firestore
    fun callAndGetCitiesFromFirestore(): LiveData<List<City>> {
        val mutableCitiesList: MutableLiveData<List<City>> = MutableLiveData<List<City>>()
        getWeatherCollection()
            .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                val citiesList: MutableList<City> = ArrayList<City>()
                if (value != null) {
                    for (document in value.documents) {
                        val city: City? = document.toObject(City::class.java)
                        citiesList.add(city!!)
                    }
                }
                mutableCitiesList.setValue(citiesList)
            }
        return mutableCitiesList
    }

    // Get All info about a City weather
    val openWeatherResponseListFromFirestore = mutableListOf<OpenWeatherResponse>()
    fun callOpenWeatherResponseFirestoreRequest(cityAndCountryName: String) {
        getWeatherInfoCollection(cityAndCountryName)
            .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
                if (value != null) {
                    for (document in value.documents) {
                        val openWeatherResponse: OpenWeatherResponse? =
                            document.toObject(OpenWeatherResponse::class.java)
                        mutableOpenWeatherResponseFromFirestore.value = openWeatherResponse!!
                    }
                }
            }
    }

    // Get a list of info about cities weather
    fun getOpenWeatherResponseFromFirestore(): LiveData<OpenWeatherResponse> {
        return mutableOpenWeatherResponseFromFirestore
    }

}