package com.instantwebb.deviceinfo.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.instantwebb.deviceinfo.databinding.FragmentLocationBinding
import java.util.*


class LocationFragment : Fragment() {
    private var _binding: FragmentLocationBinding? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    var newAddress: String? = null
    private val binding get() = _binding!!

    companion object {
        private var instance: LocationFragment? = null

        fun getInstance(): LocationFragment {

            return instance!!
        }
    }

    fun getPermissionResult(grantResults: IntArray, requestCode: Int) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        val view = binding.root
        instance = this
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getLocation()
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    try{
                        val location: Location = task.result
                        if(location != null){
                            val geocoder = Geocoder(requireContext(), Locale.getDefault())
                            val list: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 3)
                            val fullAddress = getFormattedAddress(list!!)
                            binding.tvCurrentLocation.text = fullAddress
                            binding.tvLatLon.text = "${location.latitude},${location.longitude}"
                            binding.tvCountry.text = list[0].countryName.toString()
                            binding.tvCountryCode.text = list[0].countryCode.toString()
                            binding.tvPostalCode.text = list[0].postalCode.toString()

                            var locationURL =
                                "https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}";
                            generateQRCodeImage(locationURL)
                        }else{
                            binding.tvCurrentLocation.text = ""
                            binding.tvLatLon.text = ""
                            binding.tvCountry.text = ""
                            binding.tvCountryCode.text = ""
                            binding.tvPostalCode.text = ""
                        }
                    }catch (exception: Exception){
                        binding.tvCurrentLocation.text = ""
                        binding.tvLatLon.text = ""
                        binding.tvCountry.text = ""
                        binding.tvCountryCode.text = ""
                        binding.tvPostalCode.text = ""
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
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                permissionId
            )
        }
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

    private fun generateQRCodeImage(qrCodeText: String) {
        var qrgEncoder = QRGEncoder(qrCodeText, null, QRGContents.Type.TEXT, 400)
        qrgEncoder.colorBlack = Color.WHITE
        qrgEncoder.colorWhite = Color.BLUE
        try {
            binding.ivQRCode.setImageBitmap(qrgEncoder.bitmap)
        } catch (e: java.lang.Exception) {
            Log.v(LocationFragment.javaClass.name, e.toString())
        }
    }

}