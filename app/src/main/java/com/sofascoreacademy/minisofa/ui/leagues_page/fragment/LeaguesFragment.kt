package com.sofascoreacademy.minisofa.ui.leagues_page.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofascoreacademy.minisofa.MainActivity
import com.sofascoreacademy.minisofa.databinding.FragmentLeaguesBinding
import com.sofascoreacademy.minisofa.ui.home.HomeViewModel
import com.sofascoreacademy.minisofa.ui.leagues_page.adapter.LeaguesRecyclerAdapter
import com.sofascoreacademy.minisofa.ui.util.processAsListForRecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ARG_SPORT_SLUG = "sport_slug"

class LeaguesFragment : Fragment() {

    companion object {
        fun newInstance(sportSlug: String): LeaguesFragment {
            return LeaguesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SPORT_SLUG, sportSlug)
                }
            }
        }
    }

    private var _binding: FragmentLeaguesBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by activityViewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaguesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val leaguesRecyclerAdapter = LeaguesRecyclerAdapter(requireContext())

        val sportSlug = arguments?.getString(ARG_SPORT_SLUG) ?: ""

        binding.rvLeagues.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = leaguesRecyclerAdapter
        }

        homeViewModel.leaguesLiveData.observe(viewLifecycleOwner) { binding.apply {
            it.processAsListForRecyclerView(
                rvLeagues,
                tvNoLeagues,
                tvNoConnectionLoadingLeagues,
                pbLoadingLeagues,
                leaguesRecyclerAdapter::submitList
            )
        }}

        homeViewModel.apply {
            viewModelScope.launch(Dispatchers.IO) {
                fetchLeaguesForSport(sportSlug, (requireActivity() as MainActivity)::navigateToTournamentDetailsFromHome)
            }
        }
    }
}