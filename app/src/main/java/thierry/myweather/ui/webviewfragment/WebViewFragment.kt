package thierry.myweather.ui.webviewfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import thierry.myweather.databinding.FragmentWebViewBinding

@AndroidEntryPoint
class WebViewFragment : Fragment() {
    private val viewModel: WebViewViewModel by viewModels()
    private lateinit var citySearched: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentWebViewBinding.inflate(layoutInflater)
        val rootView = binding.root

        viewModel.getCitiesFromRoom.observe(viewLifecycleOwner) { citiesList ->

            val citiesSpinner = binding.citiesSpinner
            val citiesMutableList = mutableListOf<String>()
            citiesList.forEach { city ->
                citiesMutableList.add(city.name.toString())
            }
            val citiesAdapter =
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    citiesMutableList
                )

            citiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            citiesSpinner.adapter = citiesAdapter
            citiesSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        p1: View?,
                        position: Int,
                        p3: Long,
                    ) {
                        val aSpinnerResult: String =
                            parent?.getItemAtPosition(position).toString()
                        citySearched = aSpinnerResult
                        Log.i("THIERRYBITAR", citySearched)
                        binding.webView.loadUrl("https://www.bing.com/search?q=${citySearched}+meteo")
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }

        }

        return rootView
    }

    companion object {
        fun newInstance() = WebViewFragment()
    }

}