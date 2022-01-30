package thierry.myweather.ui.cityDetailFragment

import thierry.myweather.model.OpenWeatherResponse
import thierry.myweather.model.WeatherIconUrl

data class CityDetailViewState(
    var weatherIconsUrl: List<WeatherIconUrl>? = null,
    var openWeatherResponseFromFirestore: OpenWeatherResponse? = null,
    var isFailure: Boolean? = null,
    var isConnected: Boolean? = null
)