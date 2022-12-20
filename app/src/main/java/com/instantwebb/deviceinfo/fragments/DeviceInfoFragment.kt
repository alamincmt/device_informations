package com.instantwebb.deviceinfo.fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.an.deviceinfo.device.model.Device
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.instantwebb.deviceinfo.databinding.FragmentDeviceBinding
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*


class DeviceInfoFragment : Fragment() {
    private var _binding: FragmentDeviceBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onResume() {
        super.onResume()
        getDeviceInfo()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("HardwareIds")
    private fun getDeviceInfo() {

        val device = Device(requireContext())
        binding.tvManufacturer.text = device.manufacturer
        binding.tvModel.text = device.model
        binding.tvBuildVersion.text = device.buildVersionCodeName
        binding.tvProduct.text = device.product
        binding.tvFingerprint.text = device.fingerprint
        binding.tvHardware.text = device.hardware
        binding.tvDevice.text = device.device
        binding.tvBoard.text = device.board
        binding.tvOsVersion.text = device.osVersion
        binding.tvLanguage.text = device.language
        binding.tvSdkVersion.text = device.sdkVersion.toString()
        binding.tvHeight.text = device.screenHeight.toString()
        binding.tvWidth.text = device.screenWidth.toString()
        binding.tvBuildBrand.text = device.buildBrand
        getAdId()
        binding.tvUuid.text = getUUID()
    }

    @SuppressLint("HardwareIds")
    fun getUUID(): String {
        val androidId = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val androidId_UUID = UUID.nameUUIDFromBytes(androidId.toByteArray(charset("utf8")))
        val unique_id = androidId_UUID.toString()
        return unique_id
    }

    private fun getAdId(){
        CoroutineScope(Dispatchers.IO).launch {

            var idInfo: AdvertisingIdClient.Info? = null
            try {
                idInfo = AdvertisingIdClient.getAdvertisingIdInfo(requireContext())
            } catch (e: GooglePlayServicesNotAvailableException) {
                e.printStackTrace()
            } catch (e: GooglePlayServicesRepairableException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            var advertId: String? = null
            try {
                advertId = idInfo!!.id
                if(advertId !=null){
                    requireActivity().runOnUiThread(Runnable {
                        binding.tvDeviceAdId.text = advertId
                    })
                }

            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
            Log.d(TAG, "onCreate:AD ID $advertId")

        }
    }
}