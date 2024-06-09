package com.sofascoreacademy.minisofa.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sofascoreacademy.minisofa.MainActivity
import com.sofascoreacademy.minisofa.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnGoBack.setOnClickListener {
            (requireActivity() as MainActivity).navigateBack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}