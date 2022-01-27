package thierry.myweather.ui.citiesfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import thierry.myweather.databinding.ItemCityBinding
import thierry.myweather.model.City
import thierry.myweather.model.OpenWeatherResponse
import thierry.myweather.model.WeatherIconUrl

class CitiesAdapter(
    val cities: List<City>,
    private val openWeatherResponseList: List<OpenWeatherResponse>?,
    private val weatherIconsUrl: List<WeatherIconUrl>?
) :
    RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cityName.text = cities[position].name
        holder.cityCountry.text = cities[position].countryCode
        holder.cityName.tag = cities[position].id

        openWeatherResponseList?.forEach { response ->
            if (holder.cityName.text.toString() == response.name.toString() && holder.cityCountry.text.toString() == response.sys?.country) {
                holder.progressCircularTemp.isVisible = false
                val cityTemp = "${response.main?.temp.toString()} °"
                holder.cityTemperature.text = cityTemp
                weatherIconsUrl?.forEach { weatherIconUrl ->
                    if (weatherIconUrl.name == response.weather?.get(
                            0
                        )?.icon
                    ) {
                        holder.progressCircularImage.isVisible = false
                        Glide.with(holder.itemView)
                            .load(
                                weatherIconUrl.firestoreStorageUrl
                            )
                            .centerCrop().into(holder.cityImage)
                    }
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
        val cityCountry = binding.cityCountry
        val progressCircularTemp = binding.progressCircularTemp
        val progressCircularImage = binding.progressCircularImage
    }

}