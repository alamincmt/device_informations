package com.instantwebb.deviceinfo

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.instantwebb.deviceinfo.adapter.ViewPagerAdapter
import com.instantwebb.deviceinfo.fragments.DeviceInfoFragment
import com.instantwebb.deviceinfo.fragments.LocationFragment
import com.instantwebb.deviceinfo.fragments.NetworkFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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

    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initTabs()
        setSupportActionBar(toolbar)
    }

    private fun initTabs() {

        // Initializing the ViewPagerAdapter
        val adapter = ViewPagerAdapter(this)

        // add fragment to the list
        adapter.addFragment(LocationFragment(), "Location")
        adapter.addFragment(DeviceInfoFragment(), "Device Info")
        adapter.addFragment(NetworkFragment(), "Network")

        // Adding the Adapter to the ViewPager
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            if (position == 0) {
                tab.text = "Location"
            } else if (position == 1) {
                tab.text = "Device Info"
            } else if (position == 2) {
                tab.text = "Network"
            }

        }.attach()
    }

    private fun initViews() {
        viewPager2 = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        toolbar = findViewById(R.id.toolbar)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun getDeviceIP(view: View) {

        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork
        val acN = connectivityManager.getNetworkCapabilities(currentNetwork)
        if (acN != null) {
            /*if (acN.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                tvConnectionType?.text = "Connection Type : Wifi"
                val ipAddress = getLocalIpAddress()
                val publicIp = getPublicIPAddress()
                tvDeviceIp?.text = "Your Device IP Address: $ipAddress \n Public ip: $publicIp"
            }else{
                tvConnectionType?.text = "Connection Type : Mobile Data"
                val ipAddress = getLocalIpAddress()
                val publicIp = getPublicIPAddress()
                tvDeviceIp?.text = "Your Device IP Address: $ipAddress \n Public ip: $publicIp"
            }*/
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (LocationFragment.getInstance() !=null){
            LocationFragment.getInstance().getPermissionResult(grantResults,requestCode)
        }
    }
}