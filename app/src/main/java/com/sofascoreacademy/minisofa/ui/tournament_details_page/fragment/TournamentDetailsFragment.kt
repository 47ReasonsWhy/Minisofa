package com.sofascoreacademy.minisofa.ui.tournament_details_page.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.sofascoreacademy.minisofa.MainActivity
import com.sofascoreacademy.minisofa.R
import com.sofascoreacademy.minisofa.databinding.FragmentTournamentDetailsBinding
import com.sofascoreacademy.minisofa.ui.home.HomeViewModel
import com.sofascoreacademy.minisofa.ui.tournament_details_page.adapter.TournamentDetailsViewPagerAdapter

const val ARG_TOURNAMENT = "tournament"


class TournamentDetailsFragment : Fragment() {

    private var _binding: FragmentTournamentDetailsBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by activityViewModels<HomeViewModel>()

    private val args: TournamentDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTournamentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tournament = args.tournament

        val tournamentDetailsViewPagerAdapter = TournamentDetailsViewPagerAdapter(this, tournament)

        binding.apply {
            vpTournamentDetails.adapter = tournamentDetailsViewPagerAdapter
            tvTournamentName.text = tournament.name
            tvCountryName.text = tournament.country.name

            Glide.with(this@TournamentDetailsFragment)
                .load(tournament.logoURL)
                .placeholder(R.drawable.ic_sofascore)
                .into(ivTournamentLogo)

            backButton.setOnClickListener {
                (requireActivity() as MainActivity).navigateBack()
            }

            TabLayoutMediator(tlTournamentDetails, vpTournamentDetails) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.matches)
                    1 -> tab.text = getString(R.string.standings)
                }
            }.attach()

        }

    }
}