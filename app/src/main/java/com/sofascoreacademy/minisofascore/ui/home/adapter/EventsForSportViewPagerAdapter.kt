package com.sofascoreacademy.minisofascore.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sofascoreacademy.minisofascore.ui.home.fragment.EventsForSportAndDateFragment

class EventsForSportViewPagerAdapter(fragment: Fragment, private val dateMap: Map<Int, String>)
    : FragmentStateAdapter(fragment)
{
    override fun getItemCount(): Int {
        return dateMap.size
    }

    override fun createFragment(position: Int): Fragment {
        return EventsForSportAndDateFragment()
    }
}
