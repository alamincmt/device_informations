package com.example.locationdemoproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.conn.util.InetAddressUtils
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.*
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask


open class MainActivity : AppCompatActivity() {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private var tvLocation: AppCompatTextView? = null
    private var tvDeviceIp: AppCompatTextView? = null
    private var tvConnectionType: AppCompatTextView? = null
    private var tvAddress: AppCompatTextView? = null
    var newAddress: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initViews() {
        tvLocation = findViewById(R.id.tvLocation)
        tvDeviceIp = findViewById(R.id.tvDeviceIp)
        tvConnectionType = findViewById(R.id.tvConnectionType)
        tvAddress = findViewById(R.id.tvAddress)
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val fullAddress = "Address: "+getFormattedAddress(list!!)
                        tvAddress?.text = fullAddress
                        tvLocation?.text = "Latitude: ${location.latitude}\nLongitude: ${location.longitude}," +
                                "\nCountry Code: ${list[0].countryCode}"

                    }else{
                        tvLocation?.text = "Location not found, Please try again."
                    }
                }
            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    fun getUserLocation(view: View) {
        getLocation()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getDeviceIP(view: View) {

        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork
        val acN = connectivityManager.getNetworkCapabilities(currentNetwork)
        if (acN != null) {
            if (acN.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                tvConnectionType?.text = "Connection Type : Wifi"
                val ipAddress = getLocalIpAddress()
                val publicIp = getPublicIPAddress()
                tvDeviceIp?.text = "Your Device IP Address: $ipAddress \n Public ip: $publicIp"
            }else{
                tvConnectionType?.text = "Connection Type : Mobile Data"
                val ipAddress = getLocalIpAddress()
                val publicIp = getPublicIPAddress()
                tvDeviceIp?.text = "Your Device IP Address: $ipAddress \n Public ip: $publicIp"
            }
        }
    }

    open fun getLocalIpAddress(): String? {
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

    private fun getFormattedAddress(addresses: List<Address>): String? {
        try {
            if (addresses[0].thoroughfare != null && addresses[0].thoroughfare == "Unnamed Road") {
                val str1 = addresses[1].getAddressLine(0)
                val str2 = addresses[2].getAddressLine(0)
                if (str1 != null) {
                    if (str1.contains("+") || str1.contains("Unnamed Road")) {
                        newAddress = str2
                    } else {
                        newAddress = str1
                    }

                } else {
                    if (str2.contains("+") || str2.contains("Unnamed Road")) {
                        newAddress = str1
                    } else {
                        newAddress = str2
                    }
                }

            } else {
                val str = addresses[0].getAddressLine(0)
                if (str.contains("+") || str.contains("Unnamed Road")) {
                    val str1 = addresses[1].getAddressLine(0)
                    val str2 = addresses[2].getAddressLine(0)
                    if (str1 != null) {
                        if (str1.contains("+") || str1.contains("Unnamed Road")) {
                            newAddress = str2
                        } else {
                            newAddress = str1
                        }

                    } else {
                        if (str2.contains("+") || str2.contains("Unnamed Road")) {
                            newAddress = str1
                        } else {
                            newAddress = str2
                        }
                    }
                } else {
                    newAddress = str
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return newAddress
    }

    open fun getPublicIPAddress(): String? {
        val cm = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
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
                    val toast = Toast.makeText(applicationContext, text, duration)
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

    fun getDirection(view: View) {
        intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}