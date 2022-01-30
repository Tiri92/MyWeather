package thierry.myweather.ui.cityDetailFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import thierry.myweather.R
import thierry.myweather.databinding.FragmentCityDetailBinding
import thierry.myweather.utils.Utils

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
            viewModel.found = viewModel.getOpenWeatherResponseListFromFirestore()
                .find { predicate -> predicate.name == viewModel.cityName && predicate.sys?.country == viewModel.cityCountry }
            viewModel.getOpenWeatherResponseListFromFirestore()
                .forEach { openWeatherResponseFromFirestore ->
                    if (viewModel.cityName == openWeatherResponseFromFirestore.name && viewModel.cityCountry == openWeatherResponseFromFirestore.sys?.country) {
                        binding.cityName.text = openWeatherResponseFromFirestore.name
                        binding.weatherImageviewDescription.text =
                            openWeatherResponseFromFirestore.weather?.get(0)?.description
                        val cityTemp =
                            "${openWeatherResponseFromFirestore.main?.temp.toString()} °"
                        binding.cityTemp.text = cityTemp
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
        }

        viewModel.getViewState().observe(viewLifecycleOwner) { cityDetailViewState ->
            if (cityDetailViewState.openWeatherResponseFromFirestore != null) {
                if (cityDetailViewState.openWeatherResponseFromFirestore!!.name == viewModel.cityName && cityDetailViewState.openWeatherResponseFromFirestore!!.sys?.country == viewModel.cityCountry) {
                    binding.cityName.text =
                        cityDetailViewState.openWeatherResponseFromFirestore!!.name
                    binding.weatherImageviewDescription.text =
                        cityDetailViewState.openWeatherResponseFromFirestore!!.weather?.get(0)?.description
                    val cityTemp =
                        "${cityDetailViewState.openWeatherResponseFromFirestore!!.main?.temp.toString()} °"
                    binding.cityTemp.text = cityTemp
                }
            }

            if (cityDetailViewState.weatherIconsUrl != null && cityDetailViewState.openWeatherResponseFromFirestore != null) {
                if (cityDetailViewState.openWeatherResponseFromFirestore!!.name == viewModel.cityName && cityDetailViewState.openWeatherResponseFromFirestore!!.sys?.country == viewModel.cityCountry) {
                    cityDetailViewState.weatherIconsUrl!!.forEach { weatherIconUrl ->
                        if (weatherIconUrl.name == cityDetailViewState.openWeatherResponseFromFirestore?.weather?.get(
                                0
                            )?.icon
                        ) {
                            Glide.with(rootView).load(weatherIconUrl.firestoreStorageUrl)
                                .centerCrop()
                                .into(binding.weatherImageview)
                        }
                    }
                }
            }

            if (viewModel.found != null) {
                binding.progressIndicator.hide()
                binding.errorMessage.isVisible = false
            } else {
                binding.errorMessage.isVisible = true
                binding.errorMessage.text = getString(R.string.no_city_found)
            }

            if (cityDetailViewState.isFailure == true && cityDetailViewState.isConnected == false || cityDetailViewState.isConnected == null) {
                binding.progressIndicator.show()
                Utils.displayCustomSnackbar(
                    requireView(),
                    getString(R.string.no_internet),
                    ContextCompat.getColor(requireContext(), R.color.red)
                )
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