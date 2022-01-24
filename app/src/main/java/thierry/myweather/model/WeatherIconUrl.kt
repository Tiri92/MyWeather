package thierry.myweather.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_icon_url_table")
data class WeatherIconUrl(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val firestoreStorageUrl: String
)