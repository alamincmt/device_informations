package com.instantwebb.deviceinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.instantwebb.deviceinfo.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.instantwebb.deviceinfo.fragments.*


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
        adapter.addFragment(BatteryInfoFragment(), "Battery")
        adapter.addFragment(MemoryInfoFragment(), "Memory")

        // Adding the Adapter to the ViewPager
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            if (position == 0) {
                tab.text = "Location"
            } else if (position == 1) {
                tab.text = "Device Info"
            } else if (position == 2) {
                tab.text = "Network"
            } else if (position == 3) {
                tab.text = "Battery"
            } else if (position == 4) {
                tab.text = "Memory"
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