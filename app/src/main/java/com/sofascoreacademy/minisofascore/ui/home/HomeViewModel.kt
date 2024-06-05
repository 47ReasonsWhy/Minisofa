package com.sofascoreacademy.minisofascore.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sofascoreacademy.minisofascore.data.local.entity.Event
import com.sofascoreacademy.minisofascore.data.local.entity.EventWithTeamsAndTournament
import com.sofascoreacademy.minisofascore.data.local.entity.Score
import com.sofascoreacademy.minisofascore.data.local.entity.Sport
import com.sofascoreacademy.minisofascore.data.local.entity.Team
import com.sofascoreacademy.minisofascore.data.local.entity.Tournament
import com.sofascoreacademy.minisofascore.data.local.entity.enums.EventStatus
import com.sofascoreacademy.minisofascore.data.local.entity.enums.EventWinnerCode
import com.sofascoreacademy.minisofascore.data.remote.Result
import com.sofascoreacademy.minisofascore.data.repository.MinisofaRepository
import com.sofascoreacademy.minisofascore.data.repository.Resource
import com.sofascoreacademy.minisofascore.ui.home.adapter.EventsForSportAndDateRecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _sports = MutableLiveData<Resource<List<Sport>>>()
    val sports: LiveData<Resource<List<Sport>>> = _sports

    private val _events = MutableLiveData<Resource<List<EventsForSportAndDateRecyclerAdapter.EventListItem>>>()
    val events: LiveData<Resource<List<EventsForSportAndDateRecyclerAdapter.EventListItem>>> = _events

    private val minisofaRepository = MinisofaRepository(application)


/*
    fun fetchSports() {
        viewModelScope.launch {
            minisofaRepository.fetchSports().collect {
                _sports.value = it
            }
        }
    }
*/

    fun fetchSports() {
        viewModelScope.launch {
            _sports.value = Resource.Loading()
            val sportsResult = withContext(Dispatchers.IO) { minisofaRepository.fetchSportsFromNetwork() }
            when (sportsResult) {
                is Result.Error -> {
                    _sports.value = Resource.Failure(sportsResult.error)
                }
                is Result.Success -> {
                    val sports = sportsResult.data.map { sport ->
                        Sport(sport.id, sport.name, sport.slug)
                    }
                    _sports.value = Resource.Success(sports)
                }
            }
        }
    }
/*
    fun fetchEventsForSport(sportId: Int, sportSlug: String, date: String) {
        viewModelScope.launch {
            minisofaRepository.fetchEvents(sportId, sportSlug, date).collect {
                when (it) {
                    is Resource.Loading -> {
                        withContext(Dispatchers.Main) { _events.value = Resource.Loading() }
                        if (it.data != null) {
                            val eventList = async(Dispatchers.Default) {
                                it.data.groupBy { event ->
                                    event.tournaments[0]
                                }.flatMap { (tournament, eventList) ->
                                    listOf(
                                        EventsForSportAndDateRecyclerAdapter.EventListItem.HeaderItem(
                                            tournament
                                        ),
                                        *eventList.map { event ->
                                            EventsForSportAndDateRecyclerAdapter.EventListItem.EventItem(
                                                event
                                            )
                                        }.toTypedArray()
                                    )
                                }
                            }.await()
                            withContext(Dispatchers.Main) { _events.postValue(Resource.Loading(eventList)) }
                        }
                    }
                    is Resource.Failure -> {
                        withContext(Dispatchers.Main) { _events.postValue(Resource.Failure(it.error)) }
                    }
                    is Resource.Success -> {
                        withContext(Dispatchers.Main) { _events.postValue(Resource.Loading()) }
                        val eventList = async(Dispatchers.Default) {
                            it.data.groupBy { event ->
                                event.tournaments[0]
                            }.flatMap { (tournament, eventList) ->
                                listOf(
                                    EventsForSportAndDateRecyclerAdapter.EventListItem.HeaderItem(
                                        tournament
                                    ),
                                    *eventList.map { event ->
                                        EventsForSportAndDateRecyclerAdapter.EventListItem.EventItem(
                                            event
                                        )
                                    }.toTypedArray()
                                )
                            }
                        }.await()
                        withContext(Dispatchers.Main) { _events.postValue(Resource.Success(eventList)) }
                    }
                }
            }
        }
    }
    */
    fun fetchEventsForSport(sportSlug: String, date: String) {
        viewModelScope.launch {
            _events.value = Resource.Loading()
            val eventsResult = withContext(Dispatchers.IO) {
                minisofaRepository.fetchEventsFromNetwork(
                    sportSlug,
                    date
                )
            }
            when (eventsResult) {
                is Result.Error -> {
                    _events.value = Resource.Failure(eventsResult.error)
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
                                        tournament.sport.id,
                                        tournament.country.id
                                    )
                                ),
                                *eventList.map { event ->
                                    EventsForSportAndDateRecyclerAdapter.EventListItem.EventItem(
                                        EventWithTeamsAndTournament(
                                            Event(
                                                event.id,
                                                event.slug,
                                                event.tournament.id,
                                                event.homeTeam.id,
                                                event.awayTeam.id,
                                                EventStatus.fromString(event.status),
                                                if (event.startDate == null) null else event.startDate.substring(
                                                    0,
                                                    10
                                                ),
                                                if (event.startDate == null) null else event.startDate.substring(
                                                    11,
                                                    16
                                                ),
                                                event.homeScore.let { score ->
                                                    Score(
                                                        score.total,
                                                        score.period1,
                                                        score.period2,
                                                        score.period3,
                                                        score.period4,
                                                        score.overtime
                                                    )
                                                },
                                                event.awayScore.let { score ->
                                                    Score(
                                                        score.total,
                                                        score.period1,
                                                        score.period2,
                                                        score.period3,
                                                        score.period4,
                                                        score.overtime
                                                    )
                                                },
                                                event.winnerCode?.let { code ->
                                                    EventWinnerCode.fromString(
                                                        code
                                                    )
                                                },
                                                event.round
                                            ),
                                            listOf(
                                                Team(
                                                    event.homeTeam.id,
                                                    event.homeTeam.name,
                                                    event.homeTeam.country.id
                                                )
                                            ),
                                            listOf(
                                                Team(
                                                    event.awayTeam.id,
                                                    event.awayTeam.name,
                                                    event.awayTeam.country.id
                                                )
                                            ),
                                            listOf(
                                                Tournament(
                                                    event.tournament.id,
                                                    event.tournament.name,
                                                    event.tournament.slug,
                                                    event.tournament.sport.id,
                                                    event.tournament.country.id
                                                )
                                            )
                                        )
                                    )
                                }.toTypedArray()
                            )
                        }
                    }
                    _events.value = Resource.Success(eventList)
                }
            }
        }
    }
}