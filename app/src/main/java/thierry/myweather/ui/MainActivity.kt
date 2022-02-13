package thierry.myweather.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import thierry.myweather.R
import thierry.myweather.databinding.ActivityMainBinding
import thierry.myweather.ui.citiesfragment.CitiesFragment
import thierry.myweather.ui.googlemapfragment.GoogleMapFragment
import thierry.myweather.ui.playerfragment.PlayerFragment
import thierry.myweather.ui.webviewfragment.WebViewFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connectionListener()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.weather -> {
                    openFragment(CitiesFragment.newInstance())
                    true
                }
                R.id.web_view -> {
                    openFragment(WebViewFragment.newInstance())
                    true
                }
                R.id.google_map -> {
                    openFragment(GoogleMapFragment.newInstance())
                    true
                }
                R.id.player -> {
                    openFragment(PlayerFragment.newInstance())
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
    private fun connectionListener() {
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
                    runOnUiThread {
                        viewModel.getCurrentConnectionState(true)
                        viewModel.setCurrentConnectionState()
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    runOnUiThread {
                        viewModel.getCurrentConnectionState(false)
                        viewModel.setCurrentConnectionState()
                    }
                }
            })
    }

}