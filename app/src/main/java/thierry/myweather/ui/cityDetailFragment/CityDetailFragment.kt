package thierry.myweather.ui.cityDetailFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import thierry.myweather.R
import thierry.myweather.databinding.FragmentCityDetailBinding

private const val ARG_PARAM_CITY_NAME = "city name"
private const val ARG_PARAM_CITY_COUNTRY = "city country"

@AndroidEntryPoint
class CityDetailFragment : Fragment() {

    private val viewModel: CityDetailViewModel by viewModels()
    private var isFailure: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCityDetailBinding.inflate(layoutInflater)
        val rootView = binding.root

        val bottomNav =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.isVisible = false

        arguments.let {
            viewModel.cityName = it?.getString(ARG_PARAM_CITY_NAME)
            viewModel.cityCountry = it?.getString(ARG_PARAM_CITY_COUNTRY)
        }

        if (!viewModel.getOpenWeatherResponseListFromFirestore().isNullOrEmpty()) {
            viewModel.getOpenWeatherResponseListFromFirestore()
                .forEach { openWeatherResponseFromFirestore ->
                    if (viewModel.cityName == openWeatherResponseFromFirestore.name && viewModel.cityCountry == openWeatherResponseFromFirestore.sys?.country) {
                        binding.cityName.text = openWeatherResponseFromFirestore.name
                        binding.weatherImageviewDescription.text =
                            openWeatherResponseFromFirestore.weather?.get(0)?.description
                        Glide.with(rootView)
                            .load(
                                "http://openweathermap.org/img/wn/${
                                    openWeatherResponseFromFirestore.weather?.get(
                                        0
                                    )?.icon
                                }@2x.png"
                            )
                            .centerCrop().into(binding.weatherImageview)
                        Log.i(
                            "THIERRYBITAR",
                            "${openWeatherResponseFromFirestore.main?.temp} à ${openWeatherResponseFromFirestore.name}"
                        )

                    }
                }
        } else {
            Toast.makeText(
                requireContext(),
                "No weather info available, active internet please ?",
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.getViewState().observe(viewLifecycleOwner) { cityDetailViewState ->
            if (cityDetailViewState.openWeatherResponseFromFirestore != null) {
                binding.cityName.text = cityDetailViewState.openWeatherResponseFromFirestore!!.name
                binding.weatherImageviewDescription.text =
                    cityDetailViewState.openWeatherResponseFromFirestore!!.weather?.get(0)?.description
            }

            if (cityDetailViewState.weatherIconsUrl != null) {
                cityDetailViewState.weatherIconsUrl!!.forEach { weatherIconUrl ->
                    if (weatherIconUrl.name == cityDetailViewState.openWeatherResponseFromFirestore?.weather?.get(
                            0
                        )?.icon
                    ) {
                        Glide.with(rootView).load(weatherIconUrl.firestoreStorageUrl).centerCrop()
                            .into(binding.weatherImageview)
                    }
                }
            }
        }

        viewModel.getIsFailure().observe(viewLifecycleOwner) { isFailure ->
            if (!isFailure) {
                this.isFailure = isFailure
                binding.progressIndicator.hide()
                binding.weatherImageview.isVisible = true
            } else {
                this.isFailure = isFailure
                binding.progressIndicator.show()
                binding.cityName.text = ""
                binding.weatherImageviewDescription.text = ""
                binding.weatherImageview.isVisible = false
            }
        }

        viewModel.getCurrentConnectionState().observe(viewLifecycleOwner) { isConnected ->
            if (isConnected && isFailure == true) {
                Toast.makeText(requireContext(), "Internet working again", Toast.LENGTH_LONG).show()
            } else if (isConnected) {
                binding.progressIndicator.hide()
                Toast.makeText(requireContext(), "Internet working", Toast.LENGTH_LONG).show()
            } else {
                binding.progressIndicator.show()
                Toast.makeText(requireContext(), "No internet", Toast.LENGTH_LONG).show()
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.callOpenWeatherMap(viewModel.cityName!!, viewModel.cityCountry!!)
            binding.swipeRefresh.isRefreshing = false
        }

        return rootView
    }

    companion object {
        fun newInstance(cityName: String, cityCountry: String) = CityDetailFragment().apply {
            arguments = Bundle().apply {
                putString(
                    ARG_PARAM_CITY_NAME, cityName
                )
                putString(ARG_PARAM_CITY_COUNTRY, cityCountry)
            }
        }
    }

}