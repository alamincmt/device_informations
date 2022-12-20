package com.instantwebb.deviceinfo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.an.deviceinfo.device.model.Device
import com.instantwebb.deviceinfo.databinding.FragmentBatteryInfoBinding
import com.instantwebb.deviceinfo.databinding.FragmentDeviceBinding

class BatteryInfoFragment : Fragment() {
    private var _binding: FragmentBatteryInfoBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBatteryInfoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onResume() {
        super.onResume()
        getDeviceInfo()
    }

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

    }

}