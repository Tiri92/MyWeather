package thierry.myweather.ui.citiesfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import thierry.myweather.R
import thierry.myweather.databinding.ItemCityBinding
import thierry.myweather.model.City
import thierry.myweather.model.OpenWeatherResponse

class CitiesAdapter(
    val cities: List<City>,
    private val openWeatherResponseList: List<OpenWeatherResponse>?
) :
    RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cityName.text = cities[position].name
        holder.cityName.tag = cities[position].id
        Glide.with(holder.itemView).load(R.drawable.twotone_cloud_circle_24)
            .into(holder.cityImage)

        if (openWeatherResponseList != null) {
            holder.cityTemperature.text = openWeatherResponseList.size.toString()
            openWeatherResponseList.forEach { response ->
                if (holder.cityName.text.toString() == response.name.toString()) {
                    holder.cityTemperature.text = response.main?.temp.toString()
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return cities.size
    }

    class ViewHolder(binding: ItemCityBinding) : RecyclerView.ViewHolder(binding.root) {
        val cityName = binding.cityName
        val cityImage = binding.cityImage
        val cityTemperature = binding.cityTemp
    }

}