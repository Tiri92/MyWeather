package thierry.myweather.utils

import android.content.Context
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.LocationManager
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import thierry.myweather.R
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_CHECK_SETTINGS = 111

class Utils {

    companion object {

        fun displayCustomSnackbar(view: View?, message: String?, colorRes: Int) {
            val snackbar = Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG)
            val sbView = snackbar.view
            sbView.setBackgroundColor(colorRes)
            snackbar.show()
        }

        fun epochMilliToHumanDate(epoch: Long): String {
            val date = Date(epoch * 1000L)
            val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
            return dateFormat.format(date)
        }

        private fun isGpsEnabled(locationManager: LocationManager): Boolean {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        /** Method for the GoogleMapFragment **/

        /**https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient**/
        fun checkGpsState(fragmentActivity: FragmentActivity) {
            val locationManager = ContextCompat.getSystemService(
                fragmentActivity,
                LocationManager::class.java
            )
            if (!isGpsEnabled(locationManager!!)) {
                val locationRequest = LocationRequest.create()
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                val settingsBuilder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                settingsBuilder.setAlwaysShow(true)
                val task = LocationServices.getSettingsClient(fragmentActivity)
                    .checkLocationSettings(settingsBuilder.build())
                task.addOnCompleteListener { task1: Task<LocationSettingsResponse?> ->
                    try {
                        val response =
                            task1.getResult(ApiException::class.java)
                    } catch (exception: ApiException) {
                        when (exception.statusCode) {
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                                try {
                                    val resolvable = exception as ResolvableApiException
                                    resolvable.startResolutionForResult(
                                        fragmentActivity,
                                        REQUEST_CHECK_SETTINGS
                                    )
                                } catch (e: IntentSender.SendIntentException) {
                                } catch (e: ClassCastException) {
                                }
                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                        }
                    }
                }
            }
        }

        private fun bitmapDescriptorFromVector(
            context: Context,
            vectorResId: Int,
        ): BitmapDescriptor {
            // below line is use to generate a drawable.
            val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

            // below line is use to set bounds to our vector drawable.
            vectorDrawable!!.setBounds(
                0,
                0,
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight
            )

            // below line is use to create a bitmap for our
            // drawable which we have added.
            val bitmap = Bitmap.createBitmap(
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )

            // below line is use to add bitmap in our canvas.
            val canvas = Canvas(bitmap)

            // below line is use to draw our
            // vector drawable in canvas.
            vectorDrawable.draw(canvas)

            // after generating our bitmap we are returning our bitmap.
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }

        fun addMarker(
            map: GoogleMap,
            context: Context,
            lat: Double,
            lng: Double,
            cityName: String,
            cityCountry: String
        ) {
            val option = MarkerOptions()
            option.position(LatLng(lat, lng))
            option.icon(
                bitmapDescriptorFromVector(
                    context,
                    R.drawable.baseline_location_city_24
                )
            )
            option.title(cityName)
            val marker = map.addMarker(option)
            marker!!.tag = cityCountry
        }

        /** **/

    }

}