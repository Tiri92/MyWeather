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
import thierry.myweather.utils.Utils

class CitiesAdapter(
    val cities: List<City>,
    private val openWeatherResponseList: List<OpenWeatherResponse>?,
    private val weatherIconsUrl: List<WeatherIconUrl>?,
    callback: CityClicked
) :
    RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {
    private var callback: CityClicked? = callback

    interface CityClicked {
        fun onCityClicked(cityId: Any)
    }

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
                holder.progressIndicator.isVisible = false
                val cityTemp = "${response.main?.temp.toString()} °"
                holder.cityTemperature.text = cityTemp
                if (response.dt != null) {
                    val refreshmentTimeCityWeather =
                        Utils.epochMilliToHumanDate(response.dt.toLong())
                    holder.refreshmentTimeCityWeather.text = refreshmentTimeCityWeather
                }
                weatherIconsUrl?.forEach { weatherIconUrl ->
                    if (weatherIconUrl.name == response.weather?.get(
                            0
                        )?.icon
                    ) {
                        Glide.with(holder.itemView)
                            .load(
                                weatherIconUrl.firestoreStorageUrl
                            )
                            .centerCrop().into(holder.cityImage)
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            callback?.onCityClicked(
                holder.cityName.tag
            )
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
        val progressIndicator = binding.progressIndicator
        val refreshmentTimeCityWeather = binding.refreshmentTimeCityWeather
    }

}