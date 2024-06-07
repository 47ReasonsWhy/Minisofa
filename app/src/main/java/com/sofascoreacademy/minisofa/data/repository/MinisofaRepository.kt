package com.sofascoreacademy.minisofa.data.repository

import com.sofascoreacademy.minisofa.data.remote.Network
import com.sofascoreacademy.minisofa.data.util.safeResponse

class MinisofaRepository {

    private val api = Network.getInstance()


    suspend fun fetchSports() = safeResponse { api.getSports() }

    suspend fun fetchEvents(sportSlug: String, date: String) = safeResponse { api.getEvents(sportSlug, date) }

    suspend fun fetchIncidents(eventId: Int) = safeResponse { api.getIncidents(eventId) }

}