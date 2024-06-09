package com.sofascoreacademy.minisofa.ui.event_details_page.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.R.attr.colorOnSurface
import com.google.android.material.R.attr.colorOnSurfaceVariant
import com.sofascoreacademy.minisofa.MainActivity
import com.sofascoreacademy.minisofa.R
import com.sofascoreacademy.minisofa.R.attr.colorSpecificLive
import com.sofascoreacademy.minisofa.data.model.enum.EventStatus
import com.sofascoreacademy.minisofa.data.repository.Resource
import com.sofascoreacademy.minisofa.databinding.FragmentEventDetailsBinding
import com.sofascoreacademy.minisofa.ui.home.HomeViewModel
import com.sofascoreacademy.minisofa.ui.event_details_page.adapter.IncidentListItem.Companion.getIncidentViewType
import com.sofascoreacademy.minisofa.ui.event_details_page.adapter.IncidentListRecyclerAdapter
import com.sofascoreacademy.minisofa.ui.util.setTextColorFromAttr
import kotlinx.coroutines.launch

class EventDetailsFragment : Fragment() {

    private var _binding: FragmentEventDetailsBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by activityViewModels<HomeViewModel>()

    private val incidents by lazy { homeViewModel.incidentsLiveData }

    private val incidentListAdapter by lazy { IncidentListRecyclerAdapter(requireContext()) }

    private val args: EventDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val event = args.event

        binding.apply {
            tvEventDescription.text = getString(
                R.string.event_description,
                event.tournament.sport.name,
                event.tournament.country.name,
                event.tournament.name,
                event.round
            )
            tvHomeTeam.text = event.homeTeam.name
            tvAwayTeam.text = event.awayTeam.name
            when (event.status) {
                EventStatus.NOT_STARTED -> {
                    tvScore.textSize = 24f
                    tvScore.text = event.startDate ?: getString(R.string.TBA)
                    tvTime.text = event.startTime ?: ""
                    tvScore.setTextColorFromAttr(colorOnSurface)
                    tvTime.setTextColorFromAttr(colorOnSurface)
                }

                EventStatus.IN_PROGRESS -> {
                    tvScore.textSize = 40f
                    tvScore.text =
                        getString(R.string.score, event.homeScore.total, event.awayScore.total)
                    tvTime.text = getString(R.string.minute, event.round.toString())
                    tvScore.setTextColorFromAttr(colorSpecificLive)
                    tvTime.setTextColorFromAttr(colorSpecificLive)
                }

                EventStatus.FINISHED -> {
                    tvScore.textSize = 40f
                    tvScore.text =
                        getString(R.string.score, event.homeScore.total, event.awayScore.total)
                    tvTime.text = getString(R.string.FT)
                    tvScore.setTextColorFromAttr(colorOnSurfaceVariant)
                    tvTime.setTextColorFromAttr(colorOnSurfaceVariant)
                }
            }

            Glide.with(requireContext())
                .load(event.tournament.logoURL)
                .placeholder(R.drawable.ic_sofascore)
                .into(ivTournamentLogo)

            Glide.with(requireContext())
                .load(event.homeTeam.logoURL)
                .placeholder(R.drawable.ic_sofascore)
                .into(ivHomeTeamLogo)

            Glide.with(requireContext())
                .load(event.awayTeam.logoURL)
                .placeholder(R.drawable.ic_sofascore)
                .into(ivAwayTeamLogo)

            rvIncidents.apply {
                adapter = incidentListAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            btnBack.setOnClickListener {
                (requireActivity() as MainActivity).navigateBack()
            }
        }

        incidents.observe(viewLifecycleOwner) { binding.apply {
            when (it) {
                is Resource.Failure -> {
                    pbLoadingIncidents.visibility = View.GONE
                    rvIncidents.visibility = View.GONE
                    tvNoResultsYet.visibility = View.GONE
                    tvNoConnectionLoadingIncidents.visibility = View.VISIBLE
                }
                is Resource.Loading -> {
                    llNoResultsYet.visibility = View.GONE
                    tvNoConnectionLoadingIncidents.visibility = View.GONE
                    rvIncidents.visibility = View.VISIBLE
                    pbLoadingIncidents.visibility = View.VISIBLE
                    if (it.data?.isNotEmpty() == true) {
                        incidentListAdapter.submitList(it.data)
                    }
                }
                is Resource.Success -> {
                    pbLoadingIncidents.visibility = View.GONE
                    tvNoConnectionLoadingIncidents.visibility = View.GONE
                    if (it.data.isEmpty()) {
                        rvIncidents.visibility = View.GONE
                        llNoResultsYet.visibility = View.VISIBLE
                        btnViewTournamentDetails.setOnClickListener {
                            (requireActivity() as MainActivity).navigateToTournamentDetailsFromEventDetails(event.tournament)
                        }
                    } else {
                        llNoResultsYet.visibility = View.GONE
                        rvIncidents.visibility = View.VISIBLE
                        incidentListAdapter.submitList(it.data)
                    }
                }
            }
        }}

        homeViewModel.apply { viewModelScope.launch { fetchIncidentsForEvent(event.id, event.getIncidentViewType()) } }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}