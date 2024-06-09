package com.sofascoreacademy.minisofa.ui.home.main_list_page.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sofascoreacademy.minisofa.data.model.Sport
import com.sofascoreacademy.minisofa.MainViewModel
import com.sofascoreacademy.minisofa.ui.home.main_list_page.fragment.EventsForSportFragment
import com.sofascoreacademy.minisofa.ui.home.leagues_page.fragment.LeaguesFragment

class SportsViewPagerAdapter(
    fragment: Fragment,
    private val sportList: List<Sport>,
    private val state: MainViewModel.Showing
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return sportList.size
    }

    override fun createFragment(position: Int): Fragment {
        return when (state) {
            MainViewModel.Showing.EVENTS -> EventsForSportFragment.newInstance(sportList[position])
            MainViewModel.Showing.LEAGUES -> LeaguesFragment.newInstance(sportList[position].slug)
        }
    }

}
