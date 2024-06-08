package com.sofascoreacademy.minisofa.ui.home_page

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sofascoreacademy.minisofa.data.model.Country
import com.sofascoreacademy.minisofa.data.model.Event
import com.sofascoreacademy.minisofa.data.model.Incident
import com.sofascoreacademy.minisofa.data.model.Player
import com.sofascoreacademy.minisofa.data.model.Score
import com.sofascoreacademy.minisofa.data.model.Sport
import com.sofascoreacademy.minisofa.data.model.Team
import com.sofascoreacademy.minisofa.data.model.Tournament
import com.sofascoreacademy.minisofa.data.model.enums.CardColor
import com.sofascoreacademy.minisofa.data.model.enums.EventStatus
import com.sofascoreacademy.minisofa.data.model.enums.EventWinnerCode
import com.sofascoreacademy.minisofa.data.model.enums.GoalType
import com.sofascoreacademy.minisofa.data.model.enums.IncidentType
import com.sofascoreacademy.minisofa.data.model.enums.TeamSide
import com.sofascoreacademy.minisofa.data.remote.Result
import com.sofascoreacademy.minisofa.data.remote.glide.getTeamLogoURL
import com.sofascoreacademy.minisofa.data.remote.glide.getTournamentLogoURL
import com.sofascoreacademy.minisofa.data.repository.MinisofaRepository
import com.sofascoreacademy.minisofa.data.repository.Resource
import com.sofascoreacademy.minisofa.ui.home_page.adapter.EventListRecyclerAdapter
import com.sofascoreacademy.minisofa.ui.event_details_page.adapter.IncidentListItem
import com.sofascoreacademy.minisofa.ui.event_details_page.adapter.IncidentListItem.Companion.toIncidentItemList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _sports = MutableLiveData<Resource<List<Sport>>>()
    val sports: LiveData<Resource<List<Sport>>> = _sports

    private val _events = MutableLiveData<Resource<List<EventListRecyclerAdapter.EventListItem>>>()
    val events: LiveData<Resource<List<EventListRecyclerAdapter.EventListItem>>> = _events

    private val _incidents = MutableLiveData<Resource<List<IncidentListItem>>>()
    val incidents: LiveData<Resource<List<IncidentListItem>>> = _incidents

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

    fun getEvents(sportSlug: String, date: String, onClick: (Event) -> Unit) {
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
                                EventListRecyclerAdapter.EventListItem.HeaderItem(
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
                                *(eventList.map { event ->
                                    EventListRecyclerAdapter.EventListItem.EventItem(
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
                                            event.startDate?.substring(0, 10),
                                            event.startDate?.substring(11, 16),
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
                                        ),
                                        onClick
                                    )
                                }).toTypedArray()
                            )
                        }
                    }
                    _events.postValue(Resource.Success(eventList))
                }
            }
        }
    }

    fun getIncidents(eventId: Int, viewType: IncidentListItem.ViewType) {
        viewModelScope.launch(Dispatchers.IO) {
            _incidents.postValue(Resource.Loading())
            when (val incidentsResult = minisofaRepository.fetchIncidents(eventId)) {
                is Result.Error -> {
                    _incidents.postValue(Resource.Failure(incidentsResult.error))
                }
                is Result.Success -> {
                    val incidents = withContext(Dispatchers.Default) {
                        incidentsResult.data.reversed().map { incident ->
                            when (IncidentType.fromString(incident.type)) {
                                IncidentType.CARD -> Incident.Card(
                                    incident.id,
                                    Player(
                                        incident.player?.id ?: -1,
                                        incident.player?.name ?: "",
                                        incident.player?.slug ?: "",
                                        Country(
                                            incident.player?.country?.id ?: -1,
                                            incident.player?.country?.name ?: ""
                                        ),
                                        incident.player?.position ?: ""
                                    ),
                                    incident.teamSide?.let { TeamSide.fromString(it) } ?: TeamSide.AWAY,
                                    incident.color?.let { CardColor.fromString(it) } ?: CardColor.RED,
                                    incident.time ?: -1
                                )
                                IncidentType.GOAL -> Incident.Goal(
                                    incident.id,
                                    Player(
                                        incident.player?.id ?: -1,
                                        incident.player?.name ?: "",
                                        incident.player?.slug ?: "",
                                        Country(
                                            incident.player?.country?.id ?: -1,
                                            incident.player?.country?.name ?: ""
                                        ),
                                        incident.player?.position ?: ""
                                    ),
                                    incident.scoringTeam?.let { TeamSide.fromString(it) } ?: TeamSide.AWAY,
                                    incident.homeScore ?: -1,
                                    incident.awayScore ?: -1,
                                    incident.goalType?.let { GoalType.fromString(it) } ?: GoalType.PENALTY,
                                    incident.time ?: -1
                                )
                                IncidentType.PERIOD -> Incident.Period(
                                    incident.id,
                                    incident.text ?: "",
                                    incident.time ?: -1
                                )
                            }
                        }.toIncidentItemList(viewType)
                    }
                    _incidents.postValue(Resource.Success(incidents))
                }
            }
        }
    }
}