package com.sofascoreacademy.minisofascore.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sofascoreacademy.minisofascore.data.local.entity.Sport
import com.sofascoreacademy.minisofascore.ui.home.fragment.EventsForSportFragment

class SportsViewPagerAdapter(fragment: Fragment, private val sportList: List<Sport>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return sportList.size
    }

    override fun createFragment(position: Int): Fragment {
        return EventsForSportFragment.newInstance(sportList[position])
    }
}