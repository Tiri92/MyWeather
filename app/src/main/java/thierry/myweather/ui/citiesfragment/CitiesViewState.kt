package thierry.myweather.ui.citiesfragment

import thierry.myweather.model.City
import thierry.myweather.model.OpenWeatherResponse
import thierry.myweather.model.WeatherIconUrl

data class CitiesViewState(
    var citiesList: List<City>? = null,
    var openWeatherResponseList: List<OpenWeatherResponse>? = null,
    var weatherIconsUrl: List<WeatherIconUrl>? = null,
    var isConnected: Boolean? = null

)