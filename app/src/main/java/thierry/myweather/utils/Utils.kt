package thierry.myweather.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

class Utils {

    companion object {

        fun displayCustomSnackbar(view: View?, message: String?, colorRes: Int) {
            val snackbar = Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG)
            val sbView = snackbar.view
            sbView.setBackgroundColor(colorRes)
            snackbar.show()
        }

    }

}