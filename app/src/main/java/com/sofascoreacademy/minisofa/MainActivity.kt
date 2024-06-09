package com.sofascoreacademy.minisofa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.sofascoreacademy.minisofa.data.model.Event
import com.sofascoreacademy.minisofa.databinding.ActivityMainBinding
import com.sofascoreacademy.minisofa.ui.home.HomeFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment_activity_main)
    }

    fun navigateToEventDetails(event: Event) {
        val action = HomeFragmentDirections.navigateToEventDetails(event)
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