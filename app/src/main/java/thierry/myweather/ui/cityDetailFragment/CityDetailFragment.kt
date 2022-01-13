package thierry.myweather.ui.cityDetailFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import thierry.myweather.R
import thierry.myweather.databinding.FragmentCityDetailBinding

@AndroidEntryPoint
class CityDetailFragment : Fragment() {

    private val viewModel: CityDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCityDetailBinding.inflate(layoutInflater)
        val rootView = binding.root

        val bottomNav =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.isVisible = false

        viewModel.callOpenWeatherMap("Paris", "fr")
        viewModel.getOpenWeatherResponse().observe(viewLifecycleOwner) { openWeatherResponse ->
            Log.i("THIERRYBITAR", "${openWeatherResponse.main?.temp} à ${openWeatherResponse.name}")
        }

        return rootView
    }

    companion object {
        fun newInstance() = CityDetailFragment()
    }

}