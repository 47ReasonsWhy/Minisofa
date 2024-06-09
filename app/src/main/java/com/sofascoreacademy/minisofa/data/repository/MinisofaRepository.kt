package com.sofascoreacademy.minisofa.data.repository

import com.sofascoreacademy.minisofa.data.remote.Network
import com.sofascoreacademy.minisofa.data.util.safeResponse

class MinisofaRepository {

    private val api = Network.getInstance()


    suspend fun fetchSports() = safeResponse { api.getSports() }

    suspend fun fetchEvents(sportSlug: String, date: String) = safeResponse { api.getEventsForSportAndDate(sportSlug, date) }

    suspend fun fetchTournaments(sportSlug: String) = safeResponse { api.getTournamentsForSport(sportSlug) }

    suspend fun fetchIncidents(eventId: Int) = safeResponse { api.getIncidents(eventId) }

    suspend fun fetchFutureEventsForTournament(tournamentId: Int, page: Int) = safeResponse { api.getFutureEventsForTournament(tournamentId, page) }

    suspend fun fetchPastEventsForTournament(tournamentId: Int, page: Int) = safeResponse { api.getPastEventsForTournament(tournamentId, page) }

    suspend fun fetchTournamentStandings(tournamentId: Int) = safeResponse { api.getStandings(tournamentId).first() }

}