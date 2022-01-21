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
import thierry.myweather.ui.cityDetailFragment.CityDetailFragment
import thierry.myweather.utils.Utils
import java.util.*

@AndroidEntryPoint
class CitiesFragment : Fragment() {

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

                if (!citiesViewState.openWeatherResponseList.isNullOrEmpty()) {
                    setUpRecyclerView(
                        recyclerView!!,
                        citiesViewState.citiesList!!.sortedBy { city -> city.position },
                        citiesViewState.openWeatherResponseList
                    )
                } else {
                    citiesViewState.citiesList!!.forEach { city ->
                        // viewModel.callOpenWeatherMap(city.name.toString(), "FR")
                        // viewModel.createCityInFirestore(city)
                    }
                    setUpRecyclerView(
                        recyclerView!!,
                        citiesViewState.citiesList!!.sortedBy { city -> city.position },
                        null
                    )
                }

                val adapterSortedCitiesList =
                    (binding.recyclerviewCities.adapter as CitiesAdapter).cities

                val simpleCallback = object :
                    ItemTouchHelper.SimpleCallback(
                        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
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
        }

        binding.addCityButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setView(R.layout.dialog_add_city)
                .setPositiveButton(resources.getString(R.string.add)) { dialog, _ ->
                    val typedText =
                        (dialog as androidx.appcompat.app.AlertDialog).findViewById<TextInputEditText>(
                            R.id.edittext_add_city
                        )?.text.toString()
                    if (typedText.trim().isEmpty()) {
                        Utils.displayCustomSnackbar(
                            requireView(),
                            getString(R.string.field_cant_be_empty),
                            ContextCompat.getColor(requireContext(), R.color.red)
                        )
                    } else {
                        viewModel.addCity(City(name = typedText, countryCode = "FR"))
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
        citiesList: List<City>,
        openWeatherResponseList: List<OpenWeatherResponse>?
    ) {
        val myLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = myLayoutManager
        recyclerView.adapter = CitiesAdapter(citiesList, openWeatherResponseList)
    }

    fun refreshFragment() {
        parentFragmentManager.beginTransaction().detach(this@CitiesFragment)
            .commit()
        parentFragmentManager.beginTransaction().attach(this@CitiesFragment)
            .commit()
    }

    companion object {
        fun newInstance() = CitiesFragment()
    }

}