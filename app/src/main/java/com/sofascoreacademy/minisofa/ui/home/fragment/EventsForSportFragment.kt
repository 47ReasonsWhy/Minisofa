package com.sofascoreacademy.minisofa.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sofascoreacademy.minisofa.MainActivity
import com.sofascoreacademy.minisofa.data.model.Sport
import com.sofascoreacademy.minisofa.databinding.FragmentEventsForSportBinding
import com.sofascoreacademy.minisofa.ui.home.HomeViewModel
import com.sofascoreacademy.minisofa.ui.home.adapter.viewpager.EventsForSportViewPagerAdapter
import java.time.LocalDate
import java.time.format.DateTimeFormatter


const val ARG_SPORT = "sport"

class EventsForSportFragment : Fragment() {

    companion object {
        fun newInstance(sport: Sport): EventsForSportFragment {
            return EventsForSportFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_SPORT, sport)
                }
            }
        }

        private const val KEY_CURRENT_ITEM = "current_item"
    }

    private var _binding: FragmentEventsForSportBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by activityViewModels<HomeViewModel>()

    private lateinit var sport: Sport


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEventsForSportBinding.inflate(inflater, container, false)

        return binding.root
    }

    @Suppress("Deprecation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        sport = arguments?.getSerializable(ARG_SPORT) as Sport

        val eventsForSportViewPagerAdapter = EventsForSportViewPagerAdapter(this, sport.slug, vpDateMap)

        binding.vpForSportAndDate.apply{
            isNestedScrollingEnabled = true
            adapter = eventsForSportViewPagerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    homeViewModel.getEvents(
                        sport.slug,
                        vpDateMap[position]!!,
                        (requireActivity() as MainActivity)::navigateToEventDetails
                    )
                }
            })
            currentItem = savedInstanceState?.getInt(KEY_CURRENT_ITEM) ?: 5
        }
        binding.tlDates.apply {
            tabMode = TabLayout.MODE_SCROLLABLE
        }
        TabLayoutMediator(binding.tlDates, binding.vpForSportAndDate) { tab, position ->
            tab.text = tabDateMap[position]
        }.attach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (_binding != null) outState.putInt(KEY_CURRENT_ITEM, binding.vpForSportAndDate.currentItem)
    }


    private val tabDateMap: Map<Int, String> = mutableMapOf<Int, String>().also {
        val today = LocalDate.now()
        val todayIndex = 5
        it[todayIndex] = getFormattedDate(today, true)
        for (i in 1 .. 5) {
            it[todayIndex - i] = getFormattedDate(today.minusDays(i.toLong()), false)
        }
        for (i in 1..5) {
            it[todayIndex + i] = getFormattedDate(today.plusDays(i.toLong()), false)
        }
    }

    private val vpDateMap: Map<Int, String> = mutableMapOf<Int, String>().also {
        val today = LocalDate.now()
        val todayIndex = 5
        it[todayIndex] = today.toString()
        for (i in 1 .. 5) {
            it[todayIndex - i] = today.minusDays(i.toLong()).toString()
        }
        for (i in 1..5) {
            it[todayIndex + i] = today.plusDays(i.toLong()).toString()
        }
    }

    private fun getFormattedDate(date: LocalDate, today: Boolean): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.")
        // getString(R.string.today) does not work - fragment not attached to a context
        return (if (today) "TODAY\n" else date.dayOfWeek.toString() + "\n") + formatter.format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}