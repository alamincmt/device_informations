package com.instantwebb.deviceinfo.fragments

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.instantwebb.deviceinfo.R
import com.instantwebb.deviceinfo.databinding.FragmentLocationBinding
import com.instantwebb.deviceinfo.databinding.FragmentNetworkBinding
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.*
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask

class NetworkFragment : Fragment() {
    private var _binding: FragmentNetworkBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNetworkBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        getDeviceNetworkDetails()

    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun getDeviceNetworkDetails() {

        val connectivityManager = requireContext().getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork
        val acN = connectivityManager.getNetworkCapabilities(currentNetwork)
        if (acN != null) {
            if (acN.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                binding.tvConnectionType.text = "Wifi"
            }else{
                binding.tvConnectionType.text = "Connection Type : Mobile Data"
            }
            val ipAddress = getLocalIpAddress()
            val publicIp = getPublicIPAddress()
            binding.tvIpAddress.text = ipAddress
            binding.tvPublicIpAddress.text = publicIp
        }
    }

    private fun getLocalIpAddress(): String? {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val networkInterface: NetworkInterface = en.nextElement()
                val enumerationIpAddress: Enumeration<InetAddress> = networkInterface.inetAddresses
                while (enumerationIpAddress.hasMoreElements()) {
                    val inetAddress: InetAddress = enumerationIpAddress.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return null
    }

    private fun getPublicIPAddress(): String? {
        val cm = requireContext().getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        val futureRun: FutureTask<String?> = FutureTask(object : Callable<String?> {
            @Throws(Exception::class)
            override fun call(): String? {
                if (info != null && info.isAvailable && info.isConnected) {
                    val response = StringBuilder()
                    try {
                        val urlConnection: HttpURLConnection =
                            URL("http://checkip.amazonaws.com/").openConnection() as HttpURLConnection
                        urlConnection.setRequestProperty("User-Agent", "Android-device")
                        //urlConnection.setRequestProperty("Connection", "close");
                        urlConnection.readTimeout = 15000
                        urlConnection.connectTimeout = 15000
                        urlConnection.requestMethod = "GET"
                        urlConnection.setRequestProperty("Content-type", "application/json")
                        urlConnection.connect()
                        val responseCode: Int = urlConnection.responseCode
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            val `in`: InputStream =
                                BufferedInputStream(urlConnection.getInputStream())
                            val reader = BufferedReader(InputStreamReader(`in`))
                            var line: String?
                            while (reader.readLine().also { line = it } != null) {
                                response.append(line)
                            }
                        }
                        urlConnection.disconnect()
                        return response.toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    val text = "No network is connected"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(activity, text, duration)
                    toast.show()
                    return null
                }
                return null
            }
        })
        Thread(futureRun).start()
        return try {
            futureRun.get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            null
        } catch (e: ExecutionException) {
            e.printStackTrace()
            null
        }
    }
}