package com.sofascoreacademy.minisofa.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sofascoreacademy.minisofa.HomeViewModel
import com.sofascoreacademy.minisofa.MainActivity
import com.sofascoreacademy.minisofa.R
import com.sofascoreacademy.minisofa.data.model.Sport
import com.sofascoreacademy.minisofa.data.repository.Resource
import com.sofascoreacademy.minisofa.databinding.FragmentHomeBinding
import com.sofascoreacademy.minisofa.ui.home.main_list_page.adapter.SportsViewPagerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by activityViewModels<HomeViewModel>()

    private val sports = mutableListOf<Sport>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        homeViewModel.sportsLiveData.observe(viewLifecycleOwner) {
            binding.apply {
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
                        sports.addAll(it.data)
                        pbLoadingSports.visibility = View.GONE
                        tvErrorWhileLoadingSports.visibility = View.GONE
                        tlSports.tabMode = TabLayout.MODE_FIXED
                        tlSports.visibility = View.VISIBLE
                        vpForSport.apply {
                            adapter = SportsViewPagerAdapter(this@HomeFragment, it.data, homeViewModel.state)
                            visibility = View.VISIBLE
                            registerOnPageChangeCallback(loadEventsCallback)
                        }
                        TabLayoutMediator(tlSports, vpForSport) { tab, position ->
                            if (position in 0..2) tab.setIcon(homeViewModel.sportIconMap[it.data[position].id]!!)
                            tab.text = it.data[position].name
                        }.attach()
                    }
                }
            }
        }

        homeViewModel.apply { viewModelScope.launch(Dispatchers.IO) { fetchAllSports() } }

        binding.apply {
            iwIconSettings.setOnClickListener {
                (requireActivity() as MainActivity).navigateToSettings()
            }

            iwIconLeagues.setOnClickListener {
                when (homeViewModel.state) {
                    HomeViewModel.Showing.EVENTS -> {
                        homeViewModel.state = HomeViewModel.Showing.LEAGUES
                        vpForSport.adapter = SportsViewPagerAdapter(this@HomeFragment, sports, homeViewModel.state)
                        iwIconLeagues.setImageResource(R.drawable.ic_arrow_back)
                        vpForSport.unregisterOnPageChangeCallback(loadEventsCallback)
                        vpForSport.registerOnPageChangeCallback(loadLeaguesCallback)
                    }
                    HomeViewModel.Showing.LEAGUES -> {
                        homeViewModel.state = HomeViewModel.Showing.EVENTS
                        vpForSport.adapter = SportsViewPagerAdapter(this@HomeFragment, sports, homeViewModel.state)
                        iwIconLeagues.setImageResource(R.drawable.ic_leagues)
                        vpForSport.unregisterOnPageChangeCallback(loadLeaguesCallback)
                        vpForSport.registerOnPageChangeCallback(loadEventsCallback)
                    }
                }
            }
        }
    }



    private val loadEventsCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            homeViewModel.apply {
                viewModelScope.launch(Dispatchers.IO) {
                    fetchEventsForSportAndDate(
                        sports[position].slug,
                        currentDate[sports[position].id]!!,
                        (requireActivity() as MainActivity)::navigateToTournamentDetailsFromHome,
                        (requireActivity() as MainActivity)::navigateToEventDetailsFromHome
                    )
                }
            }
        }
    }

    private val loadLeaguesCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            homeViewModel.apply {
                viewModelScope.launch(Dispatchers.IO) {
                    fetchLeaguesForSport(sports[position].slug, (requireActivity() as MainActivity)::navigateToTournamentDetailsFromHome)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
