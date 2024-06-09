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
import com.sofascoreacademy.minisofa.MainViewModel
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

    private val mainViewModel by activityViewModels<MainViewModel>()

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

        mainViewModel.sportsLiveData.observe(viewLifecycleOwner) {
            binding.apply {
                when (it) {
                    is Resource.Loading -> {
                        tlSports.visibility = View.GONE
                        vpForSport.visibility = View.GONE
                        srlHome.visibility = View.GONE
                        pbLoadingSports.visibility = View.VISIBLE
                    }

                    is Resource.Failure -> {
                        tlSports.visibility = View.GONE
                        vpForSport.visibility = View.GONE
                        pbLoadingSports.visibility = View.GONE
                        srlHome.visibility = View.VISIBLE
                        srlHome.setOnRefreshListener {
                            mainViewModel.viewModelScope.launch(Dispatchers.IO) {
                                mainViewModel.fetchAllSports()
                            }
                            srlHome.isRefreshing = false
                        }
                    }

                    is Resource.Success -> {
                        sports.addAll(it.data)
                        pbLoadingSports.visibility = View.GONE
                        srlHome.visibility = View.GONE
                        tlSports.tabMode = TabLayout.MODE_FIXED
                        tlSports.visibility = View.VISIBLE
                        vpForSport.apply {
                            adapter = SportsViewPagerAdapter(this@HomeFragment, it.data, mainViewModel.state)
                            visibility = View.VISIBLE
                            registerOnPageChangeCallback(loadEventsCallback)
                        }
                        TabLayoutMediator(tlSports, vpForSport) { tab, position ->
                            if (position in 0..2) tab.setIcon(mainViewModel.sportIconMap[it.data[position].id]!!)
                            tab.text = it.data[position].name
                        }.attach()
                    }
                }
            }
        }

        mainViewModel.apply { viewModelScope.launch(Dispatchers.IO) { fetchAllSports() } }

        binding.apply {
            iwIconSettings.setOnClickListener {
                (requireActivity() as MainActivity).navigateToSettings()
            }

            iwIconLeagues.setOnClickListener {
                when (mainViewModel.state) {
                    MainViewModel.Showing.EVENTS -> {
                        mainViewModel.state = MainViewModel.Showing.LEAGUES
                        vpForSport.adapter = SportsViewPagerAdapter(this@HomeFragment, sports, mainViewModel.state)
                        iwIconLeagues.setImageResource(R.drawable.ic_arrow_back)
                        vpForSport.unregisterOnPageChangeCallback(loadEventsCallback)
                        vpForSport.registerOnPageChangeCallback(loadLeaguesCallback)
                    }
                    MainViewModel.Showing.LEAGUES -> {
                        mainViewModel.state = MainViewModel.Showing.EVENTS
                        vpForSport.adapter = SportsViewPagerAdapter(this@HomeFragment, sports, mainViewModel.state)
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
            mainViewModel.apply {
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
            mainViewModel.apply {
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
