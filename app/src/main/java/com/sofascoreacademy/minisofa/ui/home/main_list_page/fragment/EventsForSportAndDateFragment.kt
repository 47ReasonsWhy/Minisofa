package com.sofascoreacademy.minisofa.ui.home.main_list_page.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sofascoreacademy.minisofa.MainActivity
import com.sofascoreacademy.minisofa.databinding.FragmentListEventsForSportAndDateBinding
import com.sofascoreacademy.minisofa.MainViewModel
import com.sofascoreacademy.minisofa.ui.home.main_list_page.adapter.EventListRecyclerAdapter
import com.sofascoreacademy.minisofa.ui.util.processAsListForRecyclerView
import kotlinx.coroutines.launch

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

    private val mainViewModel by activityViewModels<MainViewModel>()

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

        val eventListAdapter = EventListRecyclerAdapter(requireContext())

        binding.rwEventsForSportAndDate.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventListAdapter
        }

        binding.srlContainer.apply {
            setOnRefreshListener {
                mainViewModel.apply {
                    viewModelScope.launch {
                        fetchEventsForSportAndDate(
                            sportSlug,
                            date,
                            (requireActivity() as MainActivity)::navigateToTournamentDetailsFromHome,
                            (requireActivity() as MainActivity)::navigateToEventDetailsFromHome
                        )
                    }
                }
                isRefreshing = false
            }
        }

        mainViewModel.eventsLiveData.observe(viewLifecycleOwner) { binding.apply {
            it.processAsListForRecyclerView(
                rwEventsForSportAndDate,
                tvNoEvents,
                tvNoConnectionLoadingEvents,
                pbLoading,
                eventListAdapter::submitList
            )
        }}
    }

}
