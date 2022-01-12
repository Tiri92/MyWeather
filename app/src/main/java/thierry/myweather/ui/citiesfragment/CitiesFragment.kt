package thierry.myweather.ui.citiesfragment

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import thierry.myweather.R
import thierry.myweather.databinding.FragmentCitiesBinding
import thierry.myweather.model.City
import thierry.myweather.utils.Utils

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

        viewModel.getCities().observe(viewLifecycleOwner) { citiesList ->
            if (citiesList != null) {
                recyclerView = binding.recyclerviewCities
                setUpRecyclerView(recyclerView!!, citiesList)

                val simpleCallback = object :
                    ItemTouchHelper.SimpleCallback(
                        ItemTouchHelper.START or ItemTouchHelper.END,
                        ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
                    ) {

                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder,
                    ): Boolean {

                        return true
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        if (direction == 8) {
                            val idOfCityToDelete = viewHolder.itemView.findViewById<TextView>(
                                R.id.city_name
                            ).tag.toString().toInt()
                            viewModel.deleteCity(City(id = idOfCityToDelete))
                        }
                        if (direction == 4) {
//                            requireActivity().supportFragmentManager.beginTransaction().replace(
//                                R.id.fragment_container_view,
//                                CityDetailFragment.newInstance()
//                            ).commit()
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
                                    R.color.green
                                )
                            )
                            .addSwipeLeftActionIcon(R.drawable.ic_launcher_background)
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
                        viewModel.addCity(City(name = typedText))
                        viewModel.cityIsSuccessfullyInserted()
                            .observe(viewLifecycleOwner) {
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