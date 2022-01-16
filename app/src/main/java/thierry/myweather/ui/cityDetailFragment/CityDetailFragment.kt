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

@AndroidEntryPoint
class CityDetailFragment : Fragment() {

    private val viewModel: CityDetailViewModel by viewModels()
    private var cityName: String? = null
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
            cityName = it?.getString(ARG_PARAM_CITY_NAME)
            if (cityName != null) {
                viewModel.callOpenWeatherMap(cityName.toString(), "fr")
            }
        }

        viewModel.getOpenWeatherResponse().observe(viewLifecycleOwner) { openWeatherResponse ->
            binding.cityName.text = openWeatherResponse.name
            binding.weatherImageviewDescription.text =
                openWeatherResponse.weather?.get(0)?.description
            Glide.with(requireView())
                .load("http://openweathermap.org/img/wn/${openWeatherResponse.weather?.get(0)?.icon}@2x.png")
                .centerCrop().into(binding.weatherImageview)
            Log.i(
                "THIERRYBITAR",
                "${openWeatherResponse.main?.temp} à ${openWeatherResponse.name}"
            )
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
                viewModel.callOpenWeatherMap(cityName.toString(), "fr")
                Toast.makeText(requireContext(), "Internet working again", Toast.LENGTH_LONG).show()
            } else if (isConnected) {
                binding.progressIndicator.hide()
                Toast.makeText(requireContext(), "Internet working", Toast.LENGTH_LONG).show()
            } else {
                binding.progressIndicator.show()
                Toast.makeText(requireContext(), "No internet", Toast.LENGTH_LONG).show()
            }
        }

        return rootView
    }

    companion object {
        fun newInstance(cityName: String) = CityDetailFragment().apply {
            arguments = Bundle().apply {
                putString(
                    ARG_PARAM_CITY_NAME, cityName
                )
            }
        }
    }

}