package thierry.myweather.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_table")
data class City(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String? = null,
    var position: Int? = null
)