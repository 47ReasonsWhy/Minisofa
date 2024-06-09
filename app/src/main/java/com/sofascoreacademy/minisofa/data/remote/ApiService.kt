package com.sofascoreacademy.minisofa.data.remote

import com.sofascoreacademy.minisofa.data.remote.response.EventResponse
import com.sofascoreacademy.minisofa.data.remote.response.IncidentResponse
import com.sofascoreacademy.minisofa.data.remote.response.SportResponse
import com.sofascoreacademy.minisofa.data.remote.response.TournamentResponse
import com.sofascoreacademy.minisofa.data.remote.response.TournamentStandingsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    // https://academy-backend.sofascore.dev/api/docs#tag/Sport/operation/get_app_sports
    @GET("sports")
    suspend fun getSports(): List<SportResponse>


    // https://academy-backend.sofascore.dev/api/docs#tag/Sport/operation/get_app_sport_events
    @GET("sport/{slug}/events/{date}")
    suspend fun getEventsForSportAndDate(
        @Path("slug") slug: String,
        @Path("date") date: String
    ): List<EventResponse>

    // https://academy-backend.sofascore.dev/api/docs#tag/Sport/operation/get_app_sport_tournaments
    @GET("/sport/{slug}/tournaments")
    suspend fun getTournamentsForSport(
        @Path("slug") slug: String
    ): List<TournamentResponse>


    // https://academy-backend.sofascore.dev/api/docs#tag/Event/operation/get_app_event_incidents
    @GET("event/{id}/incidents")
    suspend fun getIncidents(
        @Path("id") id: Int
    ): List<IncidentResponse>


    // https://academy-backend.sofascore.dev/api/docs#tag/Tournament/operation/get_app_tournaments_events
    @GET("tournament/{id}/events/next/{page}")
    suspend fun getFutureEventsForTournament(
        @Path("id") id: Int,
        @Path("page") page: Int
    ): List<EventResponse>
    @GET("tournament/{id}/events/last/{page}")
    suspend fun getPastEventsForTournament(
        @Path("id") id: Int,
        @Path("page") page: Int
    ): List<EventResponse>


    // https://academy-backend.sofascore.dev/api/docs#tag/Tournament/operation/get_app_tournaments_standings
    @GET("tournament/{id}/standings")
    suspend fun getStandings(
        @Path("id") id: Int
    ): List<TournamentStandingsResponse>

}