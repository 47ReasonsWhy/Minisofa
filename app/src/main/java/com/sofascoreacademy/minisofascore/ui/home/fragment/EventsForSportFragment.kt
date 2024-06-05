package com.sofascoreacademy.minisofascore.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sofascoreacademy.minisofascore.data.local.entity.Sport
import com.sofascoreacademy.minisofascore.databinding.FragmentEventsForSportBinding
import com.sofascoreacademy.minisofascore.ui.home.HomeViewModel
import com.sofascoreacademy.minisofascore.ui.home.adapter.EventsForSportViewPagerAdapter
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
    }

    private var _binding: FragmentEventsForSportBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel by activityViewModels<HomeViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEventsForSportBinding.inflate(inflater, container, false)

        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val sport = arguments?.getSerializable(ARG_SPORT) as Sport

        val eventsForSportViewPagerAdapter = EventsForSportViewPagerAdapter(this, vpDateMap)
        binding.vpForSportAndDate.apply{
            isNestedScrollingEnabled = true
            adapter = eventsForSportViewPagerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    homeViewModel.fetchEventsForSport(sport.slug, vpDateMap[position]!!)
                }
            })
            currentItem = 5
        }
        binding.tlDates.apply {
            tabMode = TabLayout.MODE_SCROLLABLE
        }
        TabLayoutMediator(binding.tlDates, binding.vpForSportAndDate) { tab, position ->
            tab.text = tabDateMap[position]
        }.attach()
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