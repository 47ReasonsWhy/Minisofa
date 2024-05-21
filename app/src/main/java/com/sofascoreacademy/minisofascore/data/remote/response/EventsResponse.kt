package com.sofascoreacademy.minisofascore.data.remote.response

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

data class TournamentResponse(
    val id: Int,
    val name: String,
    val slug: String,
    val sport: SportResponse,
    val country: CountryResponse
)

data class CountryResponse(
    val id: Int,
    val name: String,
)

data class TeamResponse(
    val id: Int,
    val name: String,
    val country: CountryResponse
)

data class ScoreResponse(
    val total: Int,
    val period1: Int,
    val period2: Int,
    val period3: Int,
    val period4: Int,
    val overtime: Int
)
