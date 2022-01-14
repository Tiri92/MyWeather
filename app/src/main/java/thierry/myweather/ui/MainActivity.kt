package thierry.myweather.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import thierry.myweather.R
import thierry.myweather.databinding.ActivityMainBinding
import thierry.myweather.ui.citiesfragment.CitiesFragment
import thierry.myweather.utils.Utils

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connectionListener(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    openFragment(CitiesFragment.newInstance())
                    true
                }
                R.id.page_2 -> {
                    openFragment(SecondFragment.newInstance())
                    true
                }
                R.id.page_3 -> {
                    // Respond to navigation item 2 click
                    true
                }
                else -> false
            }
        }

    }

    private fun openFragment(fragmentInstance: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragmentInstance)
            .commit()
    }

    @SuppressLint("MissingPermission")
    private fun connectionListener(rootView: View) {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val builder = NetworkRequest.Builder()
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)

        val networkRequest = builder.build()
        connectivityManager.registerNetworkCallback(networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    Utils.displayCustomSnackbar(
                        rootView,
                        getString(R.string.valid_internet_connection),
                        ContextCompat.getColor(applicationContext, R.color.green)
                    )
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Utils.displayCustomSnackbar(
                        rootView,
                        getString(R.string.no_internet_connection),
                        ContextCompat.getColor(applicationContext, R.color.red)
                    )
                }
            })
    }

}