package com.sofascoreacademy.minisofascore.data.repository

import android.app.Application
import com.sofascoreacademy.minisofascore.data.local.MinisofaDatabase
import com.sofascoreacademy.minisofascore.data.local.entity.Country
import com.sofascoreacademy.minisofascore.data.local.entity.Event
import com.sofascoreacademy.minisofascore.data.local.entity.Score
import com.sofascoreacademy.minisofascore.data.local.entity.Sport
import com.sofascoreacademy.minisofascore.data.local.entity.Team
import com.sofascoreacademy.minisofascore.data.local.entity.Tournament
import com.sofascoreacademy.minisofascore.data.local.entity.enums.EventStatus
import com.sofascoreacademy.minisofascore.data.local.entity.enums.EventWinnerCode
import com.sofascoreacademy.minisofascore.data.remote.Network
import com.sofascoreacademy.minisofascore.data.util.safeResponse

class MinisofaRepository(application: Application) {

    private val api = Network.getInstance()
    private val sportDao = MinisofaDatabase.getInstance(application).sportDao()
    private val eventDao = MinisofaDatabase.getInstance(application).eventDao()
    private val tournamentDao = MinisofaDatabase.getInstance(application).tournamentDao()
    private val teamDao = MinisofaDatabase.getInstance(application).teamDao()
    private val countryDao = MinisofaDatabase.getInstance(application).countryDao()


    suspend fun fetchSports() = repoResource(
        shouldFetch = { true },
        fetch = { safeResponse { api.getSports() } },
        process = { it.map { sport -> Sport(sport.id, sport.name, sport.slug) } },
        save = { sportDao.insertSports(it) },
        load = { sportDao.getSports() }
    )

    suspend fun fetchSportsFromNetwork() = safeResponse { api.getSports() }

    suspend fun fetchEvents(sportId: Int, sportSlug: String, date: String) = repoResource(
        shouldFetch = { true },
        fetch = { safeResponse { api.getEvents(sportSlug, date) } },
        process = { EventsFetchInterResult(
            events = it.map { event ->
                Event(
                    event.id,
                    event.slug,
                    event.tournament.id,
                    event.homeTeam.id,
                    event.awayTeam.id,
                    EventStatus.fromString(event.status),
                    if (event.startDate == null) null else event.startDate.substring(0, 10),
                    if (event.startDate == null) null else event.startDate.substring(11, 16),
                    event.homeScore.let { score -> Score(score.total, score.period1, score.period2, score.period3, score.period4, score.overtime) },
                    event.awayScore.let { score -> Score(score.total, score.period1, score.period2, score.period3, score.period4, score.overtime) },
                    event.winnerCode?.let { code -> EventWinnerCode.fromString(code) },
                    event.round
                )
            },
            teams = it.flatMap { event ->
                listOf(event.homeTeam, event.awayTeam)
            }.distinct().map { team ->
                Team(team.id, team.name, team.country.id)
            },
            tournaments = it.map { event ->
                Tournament(event.tournament.id, event.tournament.name, event.tournament.slug, event.tournament.sport.id, event.tournament.country.id)
            },
            countries = it.flatMap { event ->
                listOf(event.homeTeam.country, event.awayTeam.country, event.tournament.country)
            }.distinct().map { country ->
                Country(country.id, country.name)
            }
        ) },
        save = { result ->
                    countryDao.insertCountries(result.countries)
                    teamDao.insertTeams(result.teams)
                    tournamentDao.insertTournaments(result.tournaments)
                    eventDao.insertEvents(result.events)
                },
        load = { eventDao.getEventsWithTeamsAndTournamentBySportIdAndDate(sportId, date) }
    )

    suspend fun fetchEventsFromNetwork(sportSlug: String, date: String) = safeResponse { api.getEvents(sportSlug, date) }

    data class EventsFetchInterResult(
        val events: List<Event>,
        val teams: List<Team>,
        val tournaments: List<Tournament>,
        val countries: List<Country>
    )
}