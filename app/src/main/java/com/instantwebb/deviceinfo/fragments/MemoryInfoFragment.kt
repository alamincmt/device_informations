package com.instantwebb.deviceinfo.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.an.deviceinfo.device.model.Memory
import com.instantwebb.deviceinfo.databinding.FragmentRamInfoBinding

class MemoryInfoFragment : Fragment() {
    private var _binding: FragmentRamInfoBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRamInfoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onResume() {
        super.onResume()
        getMemoryInfo()
    }

    @SuppressLint("SetTextI18n")
    private fun getMemoryInfo() {
        val memory = Memory(requireContext())
        binding.tvexternalMemeory.text = memory.isHasExternalSDCard.toString()
        binding.tvRam.text = convertToGb(memory.totalRAM).toString()+" GB"
        binding.tvInternalMemorySpace.text = convertToGb(memory.totalInternalMemorySize).toString()+" GB"
        binding.tvAvailableMemorySpace.text = convertToGb(memory.availableInternalMemorySize).toString()+" GB"
        binding.tvTotalExternalMemorySpace.text = convertToGb(memory.totalExternalMemorySize).toString()+" GB"
        binding.tvAvailableExternalMemorySpace.text = convertToGb(memory.availableExternalMemorySize).toString()+" GB"
    }

    private fun convertToGb(valInBytes: Long): Float {
        return java.lang.Float.valueOf(
            String.format(
                "%.2f",
                valInBytes.toFloat() / (1024 * 1024 * 1024)
            )
        )
    }
}