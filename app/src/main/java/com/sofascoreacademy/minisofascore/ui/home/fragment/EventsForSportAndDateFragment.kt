package com.sofascoreacademy.minisofascore.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofascoreacademy.minisofascore.data.repository.Resource
import com.sofascoreacademy.minisofascore.databinding.FragmentListEventsForSportAndDateBinding
import com.sofascoreacademy.minisofascore.ui.home.HomeViewModel
import com.sofascoreacademy.minisofascore.ui.home.adapter.EventsForSportAndDateRecyclerAdapter

class EventsForSportAndDateFragment : Fragment() {

    private var _binding: FragmentListEventsForSportAndDateBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel by activityViewModels<HomeViewModel>()

    private val events by lazy { homeViewModel.events }

    private val eventListAdapter by lazy { EventsForSportAndDateRecyclerAdapter(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListEventsForSportAndDateBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.rwEventsForSportAndDate.apply {
            adapter = eventListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        events.observe(viewLifecycleOwner) { binding.apply {
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
    }
}