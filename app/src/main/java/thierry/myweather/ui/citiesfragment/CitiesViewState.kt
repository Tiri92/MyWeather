package thierry.myweather.ui.citiesfragment

import thierry.myweather.model.City
import thierry.myweather.model.OpenWeatherResponse

data class CitiesViewState(
    var citiesList: List<City>? = null,
    var openWeatherResponseList: List<OpenWeatherResponse>? = null

)