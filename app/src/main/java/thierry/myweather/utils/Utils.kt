package thierry.myweather.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {

        fun displayCustomSnackbar(view: View?, message: String?, colorRes: Int) {
            val snackbar = Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG)
            val sbView = snackbar.view
            sbView.setBackgroundColor(colorRes)
            snackbar.show()
        }

        fun epochMilliToHumanDate(epoch: Long): String {
            val date = Date(epoch * 1000L)
            val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
            return dateFormat.format(date)
        }

    }

}