package com.sofascoreacademy.minisofa.ui.tournament_details_page.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofascoreacademy.minisofa.data.model.Tournament
import com.sofascoreacademy.minisofa.databinding.FragmentTournamentStandingsBinding
import com.sofascoreacademy.minisofa.ui.home.HomeViewModel
import com.sofascoreacademy.minisofa.ui.tournament_details_page.adapter.TournamentStandingsRecyclerAdapter
import com.sofascoreacademy.minisofa.ui.util.processAsListForRecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TournamentStandingsFragment : Fragment() {

    companion object {
        fun newInstance(tournament: Tournament): TournamentStandingsFragment {
            return TournamentStandingsFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TOURNAMENT, tournament)
                }
            }
        }
    }

    private var _binding: FragmentTournamentStandingsBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by activityViewModels<HomeViewModel>()

    private lateinit var tournament: Tournament

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTournamentStandingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        tournament = arguments?.getSerializable(ARG_TOURNAMENT) as Tournament

        val tournamentStandingsAdapter = TournamentStandingsRecyclerAdapter(requireContext())

        binding.rvTournamentStandings.apply {
            adapter = tournamentStandingsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        homeViewModel.standingsLiveData.observe(viewLifecycleOwner) { binding.apply {
            it.processAsListForRecyclerView(
                rvTournamentStandings,
                tvNoStandings,
                tvNoConnectionLoadingStandings,
                pbTournamentStandings,
                tournamentStandingsAdapter::submitList
            )
        }}

        lifecycleScope.launch(Dispatchers.IO) {
            homeViewModel.fetchTournamentStandings(tournament.id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
