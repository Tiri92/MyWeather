package thierry.myweather.ui.citiesfragment

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import thierry.myweather.R
import thierry.myweather.databinding.FragmentCitiesBinding
import thierry.myweather.model.City
import thierry.myweather.model.OpenWeatherResponse
import thierry.myweather.model.WeatherIconUrl
import thierry.myweather.ui.citydetailfragment.CityDetailFragment
import thierry.myweather.utils.Utils
import java.util.*

@AndroidEntryPoint
class CitiesFragment : CitiesAdapter.CityClicked, Fragment() {

    private val viewModel: CitiesViewModel by viewModels()
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCitiesBinding.inflate(layoutInflater)
        val rootView = binding.root

        val bottomNav =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.isVisible = true

        viewModel.getViewState().observe(viewLifecycleOwner) { citiesViewState ->
            if (citiesViewState.citiesList != null) {
                recyclerView = binding.recyclerviewCities

                recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState == 1 || newState == 2) {
                            binding.addCityButton.hide()
                        } else {
                            binding.addCityButton.show()
                        }
                    }
                })

                if (!citiesViewState.openWeatherResponseList.isNullOrEmpty()) {
                    setUpRecyclerView(
                        recyclerView!!,
                        citiesViewState.citiesList!!.sortedBy { city -> city.position },
                        citiesViewState.openWeatherResponseList,
                        citiesViewState.weatherIconsUrl
                    )
                } else {
                    setUpRecyclerView(
                        recyclerView!!,
                        citiesViewState.citiesList!!.sortedBy { city -> city.position },
                        null,
                        citiesViewState.weatherIconsUrl
                    )
                }

                val adapterSortedCitiesList =
                    (binding.recyclerviewCities.adapter as CitiesAdapter).cities

                val simpleCallback = object :
                    ItemTouchHelper.SimpleCallback(
                        0,
                        ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
                    ) {

                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder,
                    ): Boolean {

                        val fromPosition = viewHolder.absoluteAdapterPosition
                        val toPosition = target.absoluteAdapterPosition

                        Collections.swap(adapterSortedCitiesList, fromPosition, toPosition)
                        recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)

                        return true
                    }

                    override fun onSelectedChanged(
                        viewHolder: RecyclerView.ViewHolder?,
                        actionState: Int,
                    ) {
                        super.onSelectedChanged(viewHolder, actionState)
                        if (actionState == 2) {
                            viewHolder?.itemView?.setBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.background
                                )
                            )
                        }
                    }

                    override fun clearView(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                    ) {
                        super.clearView(recyclerView, viewHolder)
                        viewHolder.itemView.setBackgroundColor(0)
                        adapterSortedCitiesList.forEachIndexed { index, city ->
                            city.position = index
                            viewModel.updateCity(city)
                        }
                        refreshFragment()
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        if (direction == 8) {
                            val idOfCityToDelete = viewHolder.itemView.findViewById<TextView>(
                                R.id.city_name
                            ).tag.toString().toInt()
                            viewModel.deleteCity(City(id = idOfCityToDelete))
                            Utils.displayCustomSnackbar(
                                requireView(),
                                getString(R.string.city_successfully_deleted),
                                ContextCompat.getColor(requireContext(), R.color.red)
                            )
                        }
                        if (direction == 4) {
                            parentFragmentManager.beginTransaction().replace(
                                R.id.fragment_container_view,
                                CityDetailFragment.newInstance(
                                    viewHolder.itemView.findViewById<TextView>(
                                        R.id.city_name
                                    ).text.toString(), viewHolder.itemView.findViewById<TextView>(
                                        R.id.city_country
                                    ).text.toString()
                                )
                            ).addToBackStack("CityDetailFragment").commit()
                        }
                    }

                    override fun onChildDraw(
                        c: Canvas,
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        dX: Float,
                        dY: Float,
                        actionState: Int,
                        isCurrentlyActive: Boolean
                    ) {
                        RecyclerViewSwipeDecorator.Builder(
                            c,
                            recyclerView,
                            viewHolder,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive
                        )
                            .addSwipeRightBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.red
                                )
                            )
                            .addSwipeRightActionIcon(R.drawable.ic_baseline_delete_24)
                            .addSwipeLeftBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.cyan_200
                                )
                            )
                            .addSwipeLeftActionIcon(R.drawable.ic_baseline_cloud_24)
                            .create()
                            .decorate()
                        super.onChildDraw(
                            c,
                            recyclerView,
                            viewHolder,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive
                        )
                    }

                }

                val itemTouchHelper = ItemTouchHelper(simpleCallback)
                itemTouchHelper.attachToRecyclerView(recyclerView)
            }

            binding.swipeRefreshLayout.setOnRefreshListener {
                if (citiesViewState.isConnected == true) {
                    citiesViewState.citiesList?.forEach { city ->
                        viewModel.callOpenWeatherMap(city.name!!, city.countryCode!!)
                    }
                    Utils.displayCustomSnackbar(
                        requireView(),
                        getString(R.string.weather_info_updated),
                        ContextCompat.getColor(requireContext(), R.color.cyan_200)
                    )
                } else {
                    Utils.displayCustomSnackbar(
                        requireView(),
                        getString(R.string.no_internet_connection),
                        ContextCompat.getColor(requireContext(), R.color.red)
                    )
                }
                binding.swipeRefreshLayout.isRefreshing = false
            }

            binding.addCityButton.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setView(R.layout.dialog_add_city)
                    .setPositiveButton(resources.getString(R.string.add)) { dialog, _ ->
                        val cityNameTyped =
                            (dialog as androidx.appcompat.app.AlertDialog).findViewById<TextInputEditText>(
                                R.id.edittext_add_city
                            )?.text.toString()
                        val countryCodeTyped =
                            (dialog).findViewById<TextInputEditText>(
                                R.id.edittext_add_country
                            )?.text.toString()
                        if (cityNameTyped.trim().isEmpty() || countryCodeTyped.trim().isEmpty()) {
                            Utils.displayCustomSnackbar(
                                requireView(),
                                getString(R.string.field_cant_be_empty),
                                ContextCompat.getColor(requireContext(), R.color.red)
                            )
                        } else {
                            val newCity = City(name = cityNameTyped, countryCode = countryCodeTyped)
                            val cityFound =
                                citiesViewState.citiesList?.find { city -> city.name == newCity.name && city.countryCode == newCity.countryCode }
                            if (cityFound == null) {
                                viewModel.addCity(
                                    City(
                                        name = newCity.name,
                                        countryCode = newCity.countryCode
                                    )
                                )
                                viewModel.clearOpenWeatherResponseListFromFirestore()
                                viewModel.callOpenWeatherMap(newCity.name!!, newCity.countryCode!!)
                                viewModel.cityIsSuccessfullyInserted()
                                    .observe(viewLifecycleOwner) {
                                        refreshFragment()
                                        Utils.displayCustomSnackbar(
                                            requireView(),
                                            getString(R.string.city_successfully_added),
                                            ContextCompat.getColor(
                                                requireContext(),
                                                R.color.green
                                            )
                                        )
                                    }
                            } else {
                                Utils.displayCustomSnackbar(
                                    requireView(),
                                    getString(R.string.city_already_exist),
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.red
                                    )
                                )
                            }
                        }
                    }
                    .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
                    }
                    .show()
            }

        }

        return rootView
    }

    private fun setUpRecyclerView(
        recyclerView: RecyclerView,
        citiesList: List<City>,
        openWeatherResponseList: List<OpenWeatherResponse>?,
        weatherIconsUrl: List<WeatherIconUrl>?
    ) {
        val myLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = myLayoutManager
        recyclerView.adapter =
            CitiesAdapter(citiesList, openWeatherResponseList, weatherIconsUrl, this)
    }

    fun refreshFragment() {
        parentFragmentManager.beginTransaction().detach(this@CitiesFragment)
            .commit()
        parentFragmentManager.beginTransaction().attach(this@CitiesFragment)
            .commit()
    }

    override fun onCityClicked(cityId: Any) {
        MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.dialog_edit_city)
            .setPositiveButton(resources.getString(R.string.edit)) { dialog, _ ->
                val cityNameTyped =
                    (dialog as androidx.appcompat.app.AlertDialog).findViewById<TextInputEditText>(
                        R.id.edittext_edit_city
                    )?.text.toString()
                val countryCodeTyped =
                    (dialog).findViewById<TextInputEditText>(
                        R.id.edittext_edit_country
                    )?.text.toString()
                if (cityNameTyped.trim().isEmpty() || countryCodeTyped.trim().isEmpty()) {
                    Utils.displayCustomSnackbar(
                        requireView(),
                        getString(R.string.field_cant_be_empty),
                        ContextCompat.getColor(requireContext(), R.color.red)
                    )
                } else {
                    val cityFound =
                        viewModel.getViewState().value?.citiesList?.find { city -> city.name == cityNameTyped && city.countryCode == countryCodeTyped }
                    if (cityFound == null) {
                        viewModel.updateCity(
                            City(
                                id = cityId.toString().toInt(),
                                name = cityNameTyped,
                                countryCode = countryCodeTyped
                            )
                        )
                        if (viewModel.getViewState().value?.isConnected == true) {
                            viewModel.clearOpenWeatherResponseListFromFirestore()
                            viewModel.callOpenWeatherMap(cityNameTyped, countryCodeTyped)
                        }
                    } else {
                        Utils.displayCustomSnackbar(
                            requireView(),
                            getString(R.string.city_already_exist),
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                    }
                }
            }
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
            }
            .show()
    }

    companion object {
        fun newInstance() = CitiesFragment()
    }

}