package com.sofascoreacademy.minisofa.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sofascoreacademy.minisofa.data.model.Country
import com.sofascoreacademy.minisofa.data.model.Event
import com.sofascoreacademy.minisofa.data.model.Score
import com.sofascoreacademy.minisofa.data.model.Sport
import com.sofascoreacademy.minisofa.data.model.Team
import com.sofascoreacademy.minisofa.data.model.Tournament
import com.sofascoreacademy.minisofa.data.model.entity.enums.EventStatus
import com.sofascoreacademy.minisofa.data.model.enums.EventWinnerCode
import com.sofascoreacademy.minisofa.data.remote.Result
import com.sofascoreacademy.minisofa.data.remote.glide.getTeamLogoURL
import com.sofascoreacademy.minisofa.data.remote.glide.getTournamentLogoURL
import com.sofascoreacademy.minisofa.data.repository.MinisofaRepository
import com.sofascoreacademy.minisofa.data.repository.Resource
import com.sofascoreacademy.minisofa.ui.home.adapter.EventsForSportAndDateRecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _sports = MutableLiveData<Resource<List<Sport>>>()
    val sports: LiveData<Resource<List<Sport>>> = _sports

    private val _events = MutableLiveData<Resource<List<EventsForSportAndDateRecyclerAdapter.EventListItem>>>()
    val events: LiveData<Resource<List<EventsForSportAndDateRecyclerAdapter.EventListItem>>> = _events

    private val minisofaRepository = MinisofaRepository()


    fun getSports() {
        viewModelScope.launch(Dispatchers.IO) {
            _sports.postValue(Resource.Loading())
            when (val sportsResult = minisofaRepository.fetchSports()) {
                is Result.Error -> {
                    _sports.postValue(Resource.Failure(sportsResult.error))
                }
                is Result.Success -> {
                    val sports = withContext(Dispatchers.Default) {
                        sportsResult.data.map { sport ->
                            Sport(sport.id, sport.name, sport.slug)
                        }
                    }
                    _sports.postValue(Resource.Success(sports))
                }
            }
        }
    }

    fun getEvents(sportSlug: String, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _events.postValue(Resource.Loading())
            when (val eventsResult = minisofaRepository.fetchEvents(sportSlug, date)) {
                is Result.Error -> {
                    _events.postValue(Resource.Failure(eventsResult.error))
                }
                is Result.Success -> {
                    val eventList = withContext(Dispatchers.Default) {
                        eventsResult.data.groupBy { event ->
                            event.tournament
                        }.flatMap { (tournament, eventList) ->
                            listOf(
                                EventsForSportAndDateRecyclerAdapter.EventListItem.HeaderItem(
                                    Tournament(
                                        tournament.id,
                                        tournament.name,
                                        tournament.slug,
                                        Sport(
                                            tournament.sport.id,
                                            tournament.sport.name,
                                            tournament.sport.slug
                                        ),
                                        Country(
                                            tournament.country.id,
                                            tournament.country.name
                                        ),
                                        getTournamentLogoURL(tournament.id)
                                    )
                                ),
                                *eventList.map { event ->
                                    EventsForSportAndDateRecyclerAdapter.EventListItem.EventItem(
                                        Event(
                                            event.id,
                                            event.slug,
                                            Tournament(
                                                event.tournament.id,
                                                event.tournament.name,
                                                event.tournament.slug,
                                                Sport(
                                                    event.tournament.sport.id,
                                                    event.tournament.sport.name,
                                                    event.tournament.sport.slug
                                                ),
                                                Country(
                                                    event.tournament.country.id,
                                                    event.tournament.country.name
                                                ),
                                                getTournamentLogoURL(event.tournament.id)
                                            ),
                                            Team(
                                                event.homeTeam.id,
                                                event.homeTeam.name,
                                                Country(
                                                    event.homeTeam.country.id,
                                                    event.homeTeam.country.name
                                                ),
                                                getTeamLogoURL(event.homeTeam.id)
                                            ),
                                            Team(
                                                event.awayTeam.id,
                                                event.awayTeam.name,
                                                Country(
                                                    event.awayTeam.country.id,
                                                    event.awayTeam.country.name
                                                ),
                                                getTeamLogoURL(event.awayTeam.id)
                                            ),
                                            EventStatus.fromString(event.status),
                                            if (event.startDate == null) null else event.startDate.substring(
                                                0,
                                                10
                                            ),
                                            if (event.startDate == null) null else event.startDate.substring(
                                                11,
                                                16
                                            ),
                                            Score(
                                                event.homeScore.total,
                                                event.homeScore.period1,
                                                event.homeScore.period2,
                                                event.homeScore.period3,
                                                event.homeScore.period4,
                                                event.homeScore.overtime
                                            ),
                                            Score(
                                                event.awayScore.total,
                                                event.awayScore.period1,
                                                event.awayScore.period2,
                                                event.awayScore.period3,
                                                event.awayScore.period4,
                                                event.awayScore.overtime
                                            ),
                                            event.winnerCode?.let { code ->
                                                EventWinnerCode.fromString(
                                                    code
                                                )
                                            },
                                            event.round
                                        )
                                    )
                                }.toTypedArray()
                            )
                        }
                    }
                    _events.postValue(Resource.Success(eventList))
                }
            }
        }
    }
}