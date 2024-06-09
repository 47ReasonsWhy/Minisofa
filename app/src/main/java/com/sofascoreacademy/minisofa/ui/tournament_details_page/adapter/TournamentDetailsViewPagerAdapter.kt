package com.sofascoreacademy.minisofa.ui.tournament_details_page.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sofascoreacademy.minisofa.data.model.Tournament
import com.sofascoreacademy.minisofa.ui.tournament_details_page.fragment.TournamentMatchesFragment
import com.sofascoreacademy.minisofa.ui.tournament_details_page.fragment.TournamentStandingsFragment

class TournamentDetailsViewPagerAdapter(fragment: Fragment, private val tournament: Tournament) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TournamentMatchesFragment.newInstance(tournament)
            1 -> TournamentStandingsFragment.newInstance(tournament)
            else -> throw IllegalArgumentException("Unknown position")
        }
    }
}