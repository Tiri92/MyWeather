package thierry.myweather

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {

    companion object{
        lateinit var  instance: Application
            private set
    }
    init{
        instance = this
    }

}