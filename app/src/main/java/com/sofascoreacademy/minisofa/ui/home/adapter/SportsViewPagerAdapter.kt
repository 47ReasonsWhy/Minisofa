package com.sofascoreacademy.minisofa.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sofascoreacademy.minisofa.data.model.Sport
import com.sofascoreacademy.minisofa.ui.home.fragment.EventsForSportFragment

class SportsViewPagerAdapter(fragment: Fragment, private val sportList: List<Sport>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return sportList.size
    }

    override fun createFragment(position: Int): Fragment {
        return EventsForSportFragment.newInstance(sportList[position])
    }


}