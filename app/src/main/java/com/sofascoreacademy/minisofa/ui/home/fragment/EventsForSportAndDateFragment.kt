package com.sofascoreacademy.minisofa.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofascoreacademy.minisofa.MainActivity
import com.sofascoreacademy.minisofa.data.repository.Resource
import com.sofascoreacademy.minisofa.databinding.FragmentListEventsForSportAndDateBinding
import com.sofascoreacademy.minisofa.ui.home.HomeViewModel
import com.sofascoreacademy.minisofa.ui.home.adapter.recycler.EventListRecyclerAdapter

class EventsForSportAndDateFragment : Fragment() {

    companion object {
        fun newInstance(sportSlug: String, date: String): EventsForSportAndDateFragment {
            return EventsForSportAndDateFragment().apply {
                arguments = Bundle().apply {
                    putString("sportSlug", sportSlug)
                    putString("date", date)
                }
            }
        }
    }

    private var _binding: FragmentListEventsForSportAndDateBinding? = null

    private val binding get() = _binding!!

    private val homeViewModel by activityViewModels<HomeViewModel>()

    private val eventListAdapter by lazy { EventListRecyclerAdapter(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListEventsForSportAndDateBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val sportSlug = arguments?.getString("sportSlug") ?: ""
        val date = arguments?.getString("date") ?: ""

        binding.rwEventsForSportAndDate.apply {
            adapter = eventListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.srlContainer.apply {
            setOnRefreshListener {
                homeViewModel.getEvents(
                    sportSlug,
                    date,
                    (requireActivity() as MainActivity)::navigateToEventDetails
                )
                isRefreshing = false
            }
        }

        homeViewModel.events.observe(viewLifecycleOwner) { binding.apply {
            when (it) {
                is Resource.Failure -> {
                    pbLoading.visibility = View.GONE
                    rwEventsForSportAndDate.visibility = View.GONE
                    tvNoEvents.visibility = View.GONE
                    tvNoConnectionLoadingEvents.visibility = View.VISIBLE
                }
                is Resource.Loading -> {
                    if (it.data?.isNotEmpty() == true) {
                        pbLoading.visibility = View.VISIBLE
                        rwEventsForSportAndDate.visibility = View.VISIBLE
                        tvNoEvents.visibility = View.GONE
                        tvNoConnectionLoadingEvents.visibility = View.GONE
                        eventListAdapter.submitList(it.data)
                    } else {
                        rwEventsForSportAndDate.visibility = View.GONE
                        tvNoEvents.visibility = View.GONE
                        tvNoConnectionLoadingEvents.visibility = View.GONE
                        pbLoading.visibility = View.VISIBLE
                    }
                }
                is Resource.Success -> {
                    pbLoading.visibility = View.GONE
                    tvNoConnectionLoadingEvents.visibility = View.GONE
                    if (it.data.isEmpty()) {
                        rwEventsForSportAndDate.visibility = View.GONE
                        tvNoEvents.visibility = View.VISIBLE
                    } else {
                        tvNoEvents.visibility = View.GONE
                        rwEventsForSportAndDate.visibility = View.VISIBLE
                        eventListAdapter.submitList(it.data)
                    }
                }
            }
        }}

        homeViewModel.getEvents(
            sportSlug,
            date,
            (requireActivity() as MainActivity)::navigateToEventDetails
        )
    }
}