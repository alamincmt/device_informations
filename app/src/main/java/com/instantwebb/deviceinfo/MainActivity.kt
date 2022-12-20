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