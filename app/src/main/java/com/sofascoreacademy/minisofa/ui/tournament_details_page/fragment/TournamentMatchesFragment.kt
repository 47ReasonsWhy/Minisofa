package com.sofascoreacademy.minisofa.ui.tournament_details_page.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofascoreacademy.minisofa.MainActivity
import com.sofascoreacademy.minisofa.data.model.Tournament
import com.sofascoreacademy.minisofa.data.remote.ITEMS_PER_PAGE
import com.sofascoreacademy.minisofa.databinding.FragmentTournamentMatchesBinding
import com.sofascoreacademy.minisofa.ui.home.HomeViewModel
import com.sofascoreacademy.minisofa.ui.tournament_details_page.EventPagingSource.Companion.FIRST_PAGE
import com.sofascoreacademy.minisofa.ui.tournament_details_page.adapter.TournamentMatchesPagingAdapter
import java.time.format.DateTimeFormatter

class TournamentMatchesFragment : Fragment() {

    companion object {
        fun newInstance(tournament: Tournament): TournamentMatchesFragment {
            return TournamentMatchesFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TOURNAMENT, tournament)
                }
            }
        }
    }

    private var _binding: FragmentTournamentMatchesBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by activityViewModels<HomeViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTournamentMatchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tournament = arguments?.getSerializable(ARG_TOURNAMENT) as Tournament

        val tournamentMatchesPagingAdapter = TournamentMatchesPagingAdapter(
            requireContext(),
            DateTimeFormatter.ofPattern(homeViewModel.getDateFormatPattern())
        )

        binding.rvTournamentMatches.apply {
            scrollToPosition(FIRST_PAGE * ITEMS_PER_PAGE)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tournamentMatchesPagingAdapter
        }

        homeViewModel.observePagedTournamentEventsLiveData(
            tournament.id,
            (requireActivity() as MainActivity)::navigateToEventDetailsFromTournamentDetails,
            viewLifecycleOwner,
            tournamentMatchesPagingAdapter::submitData
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        homeViewModel.removeObservers(viewLifecycleOwner)
    }

}
