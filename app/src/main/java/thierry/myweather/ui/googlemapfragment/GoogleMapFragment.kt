package thierry.myweather.ui.googlemapfragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import thierry.myweather.R
import thierry.myweather.databinding.FragmentGoogleMapsBinding
import thierry.myweather.ui.citydetailfragment.CityDetailFragment
import thierry.myweather.utils.Utils

private const val LOCATION_REQUEST_INTERVAL_MS = 10000
private const val SMALLEST_DISPLACEMENT_THRESHOLD_METER = 25f

@AndroidEntryPoint
class GoogleMapFragment : Fragment() {
    private val viewModel: GoogleMapViewModel by viewModels()
    private var map: GoogleMap? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        googleMap.uiSettings.isZoomControlsEnabled = true
        displayMarkersOnCitiesPosition()
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        googleMap.setOnMarkerClickListener { marker ->
            parentFragmentManager.beginTransaction().replace(
                R.id.fragment_container_view,
                CityDetailFragment.newInstance(
                    marker.title.toString(), marker.tag.toString()
                )
            ).addToBackStack("CityDetailFragment").commit()
            false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGoogleMapsBinding.inflate(layoutInflater)
        val rootView = binding.root

        val bottomNav =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.isVisible = true

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == 0 && grantResults[0] != -1 && viewModel.getCurrentConnectionState.value == true) {
            Utils.checkGpsState(requireActivity())
            launchGeolocationRequest()
            map!!.isMyLocationEnabled = true
        } else if (requestCode == 0 && grantResults[0] == -1) {
            Utils.displayCustomSnackbar(
                requireView(),
                getString(R.string.allow_geolocation),
                ContextCompat.getColor(requireContext(), R.color.red)
            )
        } else {
            Utils.displayCustomSnackbar(
                requireView(),
                getString(R.string.no_internet_connection),
                ContextCompat.getColor(requireContext(), R.color.red)
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun launchGeolocationRequest() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient!!.removeLocationUpdates(locationCallback)
        fusedLocationClient!!.requestLocationUpdates(
            LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(SMALLEST_DISPLACEMENT_THRESHOLD_METER)
                .setInterval(LOCATION_REQUEST_INTERVAL_MS.toLong()),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            viewModel.currentPosition = viewModel.setLocationInLatLng(locationResult.lastLocation)
            moveAndDisplayUserPosition(viewModel.currentPosition!!)
        }
    }

    private fun moveAndDisplayUserPosition(location: LatLng) {
        val cameraPosition =
            CameraPosition.Builder().target(location)
                .zoom(5f).tilt(30f).bearing(0f).build()
        map!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun displayMarkersOnCitiesPosition() {
        for (response in viewModel.getOpenWeatherResponseListFromFirestore) {
            if (map != null) {
                Utils.addMarker(
                    map!!,
                    requireContext(),
                    response.coord?.lat!!,
                    response.coord.lon!!,
                    response.name!!,
                    response.sys?.country!!
                )
            }
        }
    }

    companion object {
        fun newInstance() = GoogleMapFragment()
    }

}