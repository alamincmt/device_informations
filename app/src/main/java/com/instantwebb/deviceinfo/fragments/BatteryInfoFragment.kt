package com.instantwebb.deviceinfo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.an.deviceinfo.device.model.Battery
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
        getBatteryInfo()
    }

    private fun getBatteryInfo() {
        val battery = Battery(requireContext())
        binding.tvBatteryPercentage.text = battery.batteryPercent.toString()
        binding.tvPhoneCharging.text = battery.isPhoneCharging.toString()
        binding.tvBatteryHealth.text = battery.batteryHealth.toString()
        binding.tvBatteryTechnology.text = battery.batteryTechnology
        binding.tvTemperature.text = battery.batteryTemperature.toString()
        binding.tvVoltage.text = battery.batteryVoltage.toString()
        binding.tvChargingSource.text = battery.chargingSource
    }
}