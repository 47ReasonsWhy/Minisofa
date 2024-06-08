package com.sofascoreacademy.minisofa.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sofascoreacademy.minisofa.MainActivity
import com.sofascoreacademy.minisofa.data.local.datastore.DateFormat
import com.sofascoreacademy.minisofa.data.local.datastore.ThemeMode
import com.sofascoreacademy.minisofa.data.local.datastore.getDateFormatPreference
import com.sofascoreacademy.minisofa.data.local.datastore.getThemeModePreference
import com.sofascoreacademy.minisofa.data.local.datastore.setDateFormatPreference
import com.sofascoreacademy.minisofa.data.local.datastore.setThemeModePreference
import com.sofascoreacademy.minisofa.databinding.FragmentSettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        lifecycleScope.launch(Dispatchers.IO) {
            val themePreference = requireContext().getThemeModePreference()
            withContext(Dispatchers.Main) {
                when (themePreference) {
                    ThemeMode.LIGHT -> binding.rbLight.isChecked = true
                    ThemeMode.DARK -> binding.rbDark.isChecked = true
                    ThemeMode.SYSTEM -> binding.rbSystem.isChecked = true
                }
            }

            val dateFormatPreference = requireContext().getDateFormatPreference()
            withContext(Dispatchers.Main) {
                when (dateFormatPreference) {
                    DateFormat.DD_MM_YYYY -> binding.rbDdMmYyyy.isChecked = true
                    DateFormat.MM_DD_YYYY -> binding.rbMmDdYyyy.isChecked = true
                    DateFormat.YYYY_MM_DD -> binding.rbYyyyMmDd.isChecked = true
                }
            }
        }

        val mapCheckedIdToThemeMode = mapOf(
            binding.rbLight.id to ThemeMode.LIGHT,
            binding.rbDark.id to ThemeMode.DARK,
            binding.rbSystem.id to ThemeMode.SYSTEM
        )

        val mapCheckedIdToDateFormat = mapOf(
            binding.rbDdMmYyyy.id to DateFormat.DD_MM_YYYY,
            binding.rbMmDdYyyy.id to DateFormat.MM_DD_YYYY,
            binding.rbYyyyMmDd.id to DateFormat.YYYY_MM_DD
        )

        binding.rgTheme.setOnCheckedChangeListener { _, checkedId ->
            val themeMode = mapCheckedIdToThemeMode[checkedId] ?: ThemeMode.SYSTEM
            lifecycleScope.launch(Dispatchers.IO) {
                requireContext().setThemeModePreference(themeMode)
            }
        }

        binding.rgDateFormat.setOnCheckedChangeListener { _, checkedId ->
            val dateFormat = mapCheckedIdToDateFormat[checkedId] ?: DateFormat.DD_MM_YYYY
            lifecycleScope.launch(Dispatchers.IO) {
                requireContext().setDateFormatPreference(dateFormat)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}