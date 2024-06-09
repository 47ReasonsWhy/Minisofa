package com.sofascoreacademy.minisofa

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.sofascoreacademy.minisofa.data.local.datastore.setThemeModeBasedOnPreference
import com.sofascoreacademy.minisofa.data.local.datastore.themeModeFlow
import com.sofascoreacademy.minisofa.data.model.Event
import com.sofascoreacademy.minisofa.data.model.Tournament
import com.sofascoreacademy.minisofa.databinding.ActivityMainBinding
import com.sofascoreacademy.minisofa.ui.event_details_page.fragment.EventDetailsFragmentDirections
import com.sofascoreacademy.minisofa.ui.home.HomeFragmentDirections
import com.sofascoreacademy.minisofa.ui.tournament_details_page.fragment.TournamentDetailsFragmentDirections
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment_activity_main)

        lifecycleScope.launch {
            themeModeFlow.collect {
                setThemeModeBasedOnPreference()
            }
        }

        mainViewModel.observeDateFormatPreference(lifecycleScope)
    }

    fun navigateToEventDetailsFromHome(event: Event) {
        val action = HomeFragmentDirections.navigateToEventDetailsFromHome(event)
        navController.navigate(action)
    }

    fun navigateToTournamentDetailsFromHome(tournament: Tournament) {
        val action = HomeFragmentDirections.navigateToTournamentDetailsFromHome(tournament)
        navController.navigate(action)
    }

    fun navigateToEventDetailsFromTournamentDetails(event: Event) {
        val action = TournamentDetailsFragmentDirections.navigateToEventDetailsFromTournamentDetails(event)
        navController.navigate(action)
    }

    fun navigateToTournamentDetailsFromEventDetails(tournament: Tournament) {
        val action = EventDetailsFragmentDirections.navigateToTournamentDetailsFromEventDetails(tournament)
        navController.navigate(action)
    }

    fun navigateToSettings() {
        val action = HomeFragmentDirections.navigateToSettings()
        navController.navigate(action)
    }

    fun navigateBack() {
        navController.popBackStack()
    }

}