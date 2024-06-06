package com.sofascoreacademy.minisofa.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.sofascoreacademy.minisofa.data.repository.Resource
import com.sofascoreacademy.minisofa.databinding.FragmentHomeBinding
import com.sofascoreacademy.minisofa.ui.home.adapter.SportsViewPagerAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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
        homeViewModel.sports.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.tlSports.visibility = View.GONE
                    binding.vpForSport.visibility = View.GONE
                    binding.tvErrorWhileLoadingSports.visibility = View.GONE
                    binding.pbLoadingSports.visibility = View.VISIBLE
                }
                is Resource.Failure -> {
                    binding.tlSports.visibility = View.GONE
                    binding.vpForSport.visibility = View.GONE
                    binding.pbLoadingSports.visibility = View.GONE
                    binding.tvErrorWhileLoadingSports.visibility = View.VISIBLE
                    Log.e("HomeFragment", "Sports fetch error: ${it.error}")
                }
                is Resource.Success -> {
                    binding.pbLoadingSports.visibility = View.GONE
                    binding.tvErrorWhileLoadingSports.visibility = View.GONE
                    binding.tlSports.visibility = View.VISIBLE
                    binding.vpForSport.apply {
                        adapter = SportsViewPagerAdapter(this@HomeFragment, it.data)
                        visibility = View.VISIBLE
                    }
                    TabLayoutMediator(binding.tlSports, binding.vpForSport) { tab, position ->
                        tab.text = it.data[position].name
                    }.attach()
                }
            }
        }

        homeViewModel.getSports()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}