package com.sofascoreacademy.minisofa.data.remote.response

// https://academy-backend.sofascore.dev/api/docs#tag/Sport/operation/get_app_sport_events
data class EventResponse(
    val id: Int,
    val slug: String,
    val tournament: TournamentResponse,
    val homeTeam: TeamResponse,
    val awayTeam: TeamResponse,
    val status: String,
    val startDate: String?,
    val homeScore: ScoreResponse,
    val awayScore: ScoreResponse,
    val winnerCode: String?,
    val round: Int
)
