package thierry.myweather.ui.citiesfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import thierry.myweather.R
import thierry.myweather.databinding.FragmentCitiesBinding
import thierry.myweather.model.City
import thierry.myweather.utils.Utils

@AndroidEntryPoint
class CitiesFragment : Fragment() {

    private val viewModel: CitiesViewModel by viewModels()
    private var newIdCityTable: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCitiesBinding.inflate(layoutInflater)
        val rootView = binding.root

        viewModel.getCities().observe(this) { citiesList ->
            if (!citiesList.isNullOrEmpty()) {
                val recyclerView = binding.recyclerviewCities
                setUpRecyclerView(recyclerView, citiesList)
            }
        }

        viewModel.getNewIdCityTable.observe(viewLifecycleOwner) { newIdCityTable ->
            this.newIdCityTable = newIdCityTable
        }

        binding.addCityButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setView(R.layout.dialog_add_city)
                .setPositiveButton(resources.getString(R.string.add)) { dialog, _ ->
                    val typedText =
                        (dialog as androidx.appcompat.app.AlertDialog).findViewById<TextInputEditText>(
                            R.id.edittext_add_city
                        )?.text.toString()
                    viewModel.addCity(City(name = typedText))
                    viewModel.cityIsSuccessfullyInserted()
                        .observe(viewLifecycleOwner) { cityIsSuccessfullyInserted ->
                            if (cityIsSuccessfullyInserted.toInt() == newIdCityTable) {
                                Utils.displayCustomSnackbar(
                                    requireView(),
                                    getString(R.string.city_successfully_added),
                                    ContextCompat.getColor(requireContext(), R.color.green)
                                )
                            } else {
                                Utils.displayCustomSnackbar(
                                    requireView(),
                                    getString(R.string.error_city_not_added),
                                    ContextCompat.getColor(requireContext(), R.color.red)
                                )
                            }
                        }
                }
                .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
                }
                .show()
        }

        return rootView
    }

    private fun setUpRecyclerView(
        recyclerView: RecyclerView,
        citiesList: List<City>
    ) {
        val myLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = myLayoutManager
        recyclerView.adapter = CitiesAdapter(citiesList)
    }

    companion object {
        fun newInstance() = CitiesFragment()
    }

}