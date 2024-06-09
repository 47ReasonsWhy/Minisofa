package com.sofascoreacademy.minisofa.ui.home.adapter.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sofascoreacademy.minisofa.ui.home.fragment.EventsForSportAndDateFragment

class EventsForSportViewPagerAdapter(fragment: Fragment, private val sportSlug: String, private val dateMap: Map<Int, String>)
    : FragmentStateAdapter(fragment)
{
    override fun getItemCount(): Int {
        return dateMap.size
    }

    override fun createFragment(position: Int): Fragment {
        return EventsForSportAndDateFragment.newInstance(sportSlug, dateMap[position]!!)
    }
}
