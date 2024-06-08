package com.sofascoreacademy.minisofa.ui.home_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.sofascoreacademy.minisofa.MainActivity
import com.sofascoreacademy.minisofa.data.repository.Resource
import com.sofascoreacademy.minisofa.databinding.FragmentHomeBinding
import com.sofascoreacademy.minisofa.ui.home_page.adapter.SportsViewPagerAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by activityViewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        homeViewModel.sports.observe(viewLifecycleOwner) { binding.apply {
            when (it) {
                is Resource.Loading -> {
                    tlSports.visibility = View.GONE
                    vpForSport.visibility = View.GONE
                    tvErrorWhileLoadingSports.visibility = View.GONE
                    pbLoadingSports.visibility = View.VISIBLE
                }

                is Resource.Failure -> {
                    tlSports.visibility = View.GONE
                    vpForSport.visibility = View.GONE
                    pbLoadingSports.visibility = View.GONE
                    tvErrorWhileLoadingSports.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    pbLoadingSports.visibility = View.GONE
                    tvErrorWhileLoadingSports.visibility = View.GONE
                    tlSports.visibility = View.VISIBLE
                    vpForSport.apply {
                        adapter = SportsViewPagerAdapter(this@HomeFragment, it.data)
                        visibility = View.VISIBLE
                    }
                    TabLayoutMediator(tlSports, vpForSport) { tab, position ->
                        tab.text = it.data[position].name
                    }.attach()
                }
            }
        }}

        homeViewModel.getSports()

        binding.iwIconSettings.setOnClickListener {
            (requireActivity() as MainActivity).navigateToSettings()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}