package thierry.myweather.ui.citiesfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import thierry.myweather.databinding.ItemCityBinding
import thierry.myweather.model.City

class CitiesAdapter(val cities: List<City>) :
    RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cityName.text = cities[position].name
        holder.cityName.tag = cities[position].id
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    class ViewHolder(binding: ItemCityBinding) : RecyclerView.ViewHolder(binding.root) {
        val cityName = binding.cityName
        val cityImage = binding.cityImage
    }

}