package com.sofascoreacademy.minisofa.data.remote.response.mapper

import com.sofascoreacademy.minisofa.data.model.Country
import com.sofascoreacademy.minisofa.data.model.Event
import com.sofascoreacademy.minisofa.data.model.Incident
import com.sofascoreacademy.minisofa.data.model.Player
import com.sofascoreacademy.minisofa.data.model.Score
import com.sofascoreacademy.minisofa.data.model.Sport
import com.sofascoreacademy.minisofa.data.model.Team
import com.sofascoreacademy.minisofa.data.model.Tournament
import com.sofascoreacademy.minisofa.data.model.TournamentStanding
import com.sofascoreacademy.minisofa.data.model.TournamentStandingInfo
import com.sofascoreacademy.minisofa.data.model.enum.CardColor
import com.sofascoreacademy.minisofa.data.model.enum.EventStatus
import com.sofascoreacademy.minisofa.data.model.enum.EventWinnerCode
import com.sofascoreacademy.minisofa.data.model.enum.GoalType
import com.sofascoreacademy.minisofa.data.model.enum.StandingsType
import com.sofascoreacademy.minisofa.data.model.enum.TeamSide
import com.sofascoreacademy.minisofa.data.remote.glide.getTeamLogoURL
import com.sofascoreacademy.minisofa.data.remote.glide.getTournamentLogoURL
import com.sofascoreacademy.minisofa.data.remote.response.CountryResponse
import com.sofascoreacademy.minisofa.data.remote.response.EventResponse
import com.sofascoreacademy.minisofa.data.remote.response.IncidentResponse
import com.sofascoreacademy.minisofa.data.remote.response.PlayerResponse
import com.sofascoreacademy.minisofa.data.remote.response.ScoreResponse
import com.sofascoreacademy.minisofa.data.remote.response.SportResponse
import com.sofascoreacademy.minisofa.data.remote.response.TeamResponse
import com.sofascoreacademy.minisofa.data.remote.response.TournamentResponse
import com.sofascoreacademy.minisofa.data.remote.response.TournamentStandingResponse
import com.sofascoreacademy.minisofa.data.remote.response.TournamentStandingsResponse


fun CountryResponse.toCountry() = Country(
    id,
    name
)



fun SportResponse.toSport() = Sport(
    id,
    name.shorten(),
    slug
)

// Could not figure out how else to get "Am. Football" instead of "American Football"
private fun String.shorten(): String {
    return if (split(" ").size > 1) {
        val split = split(" ")
        return split.map {
            it.substring(0, 2) + ". "
        }.dropLast(1).joinToString() + split.last()
    } else this
}



fun TournamentResponse.toTournament() = Tournament(
    id,
    name,
    slug,
    sport.toSport(),
    country.toCountry(),
    getTournamentLogoURL(id)
)



fun TeamResponse.toTeam() = Team(
    id,
    name,
    country.toCountry(),
    getTeamLogoURL(id)
)



fun ScoreResponse.toScore() = Score(
    total,
    period1,
    period2,
    period3,
    period4,
    overtime
)



fun EventResponse.toEvent() = Event(
    id,
    slug,
    tournament.toTournament(),
    homeTeam.toTeam(),
    awayTeam.toTeam(),
    EventStatus.fromString(status),
    startDate?.substring(0, 10),
    startDate?.substring(11, 16),
    homeScore.toScore(),
    awayScore.toScore(),
    winnerCode?.let { code -> EventWinnerCode.fromString(code) },
    round
)



fun PlayerResponse.toPlayer() = Player(
    id,
    name,
    slug,
    country.toCountry(),
    position
)



fun IncidentResponse.toCardIncident() = Incident.Card(
    id,
    player?.toPlayer() ?: throw IllegalArgumentException("Player cannot be null for card incident"),
    teamSide?.let { TeamSide.fromString(it) } ?: throw IllegalArgumentException("Team side cannot be null for card incident"),
    color?.let { CardColor.fromString(it) } ?: throw IllegalArgumentException("Color cannot be null for card incident"),
    time ?: throw IllegalArgumentException("Time cannot be null for card incident")
)

fun IncidentResponse.toGoalIncident() = Incident.Goal(
    id,
    player?.toPlayer() ?: throw IllegalArgumentException("Player cannot be null for goal incident"),
    scoringTeam?.let { TeamSide.fromString(it) } ?: throw IllegalArgumentException("Scoring team cannot be null for goal incident"),
    homeScore ?: throw IllegalArgumentException("Home score cannot be null for goal incident"),
    awayScore ?: throw IllegalArgumentException("Away score cannot be null for goal incident"),
    goalType?.let { GoalType.fromString(it) } ?: throw IllegalArgumentException("Goal type cannot be null for goal incident"),
    time ?: throw IllegalArgumentException("Time cannot be null for goal incident")
)

fun IncidentResponse.toPeriodIncident() = Incident.Period(
    id,
    text ?: throw IllegalArgumentException("Text cannot be null for period incident"),
    time ?: throw IllegalArgumentException("Time cannot be null for period incident")
)



fun TournamentStandingResponse.toTournamentStanding(info: TournamentStandingsResponse) = TournamentStanding(
    TournamentStandingInfo(
        info.id,
        info.tournament.toTournament(),
        StandingsType.fromString(info.type)
    ),
    id,
    team.toTeam(),
    points,
    scoresFor,
    scoresAgainst,
    played,
    wins,
    draws,
    losses,
    percentage
)
