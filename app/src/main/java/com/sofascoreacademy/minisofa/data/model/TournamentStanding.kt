package com.sofascoreacademy.minisofa.data.model

import com.sofascoreacademy.minisofa.data.model.enum.StandingsType
import java.io.Serializable

data class TournamentStandingInfo(
    val id: Int,
    val tournament: Tournament,
    val type: StandingsType
) : Serializable

data class TournamentStanding(
    val info: TournamentStandingInfo,
    val id: Int,
    val team: Team,
    val points: Int?,
    val scoresFor: Int,
    val scoresAgainst: Int,
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val percentage: Double?
) : Serializable

fun createTournamentStandingHeader(sport: Sport): TournamentStanding = TournamentStanding(
    TournamentStandingInfo(-1,
        Tournament(-1, "", "", sport, Country(-1, ""), ""),
        StandingsType.TOTAL
    ),
    -1,
    Team(-1, "", Country(-1, ""), ""),
    null,
    0,
    0,
    0,
    0,
    0,
    0,
    null
)
