package com.sofascoreacademy.minisofa.data.remote.response

import java.io.Serializable


// https://academy-backend.sofascore.dev/api/docs#tag/Tournament/operation/get_app_tournaments_standings

data class TournamentStandingsResponse(
    val id: Int,
    val tournament: TournamentResponse,
    val type: String,
    val sortedStandingsRows: List<TournamentStandingResponse>
) : Serializable

data class TournamentStandingResponse(
    val id: Int,
    val team: TeamResponse,
    val points: Int?,
    val scoresFor: Int,
    val scoresAgainst: Int,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val percentage: Double?
) : Serializable
