package com.sofascoreacademy.minisofa

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import androidx.paging.map
import com.sofascoreacademy.minisofa.data.local.datastore.DateFormat
import com.sofascoreacademy.minisofa.data.local.datastore.dateFormatFlow
import com.sofascoreacademy.minisofa.data.local.datastore.getDateFormatPattern
import com.sofascoreacademy.minisofa.data.local.datastore.getDayAndMonthDateFormatPattern
import com.sofascoreacademy.minisofa.data.model.Event
import com.sofascoreacademy.minisofa.data.model.Sport
import com.sofascoreacademy.minisofa.data.model.Tournament
import com.sofascoreacademy.minisofa.data.model.TournamentStanding
import com.sofascoreacademy.minisofa.data.model.createTournamentStandingHeader
import com.sofascoreacademy.minisofa.data.model.enum.IncidentType
import com.sofascoreacademy.minisofa.data.remote.ITEMS_PER_PAGE
import com.sofascoreacademy.minisofa.data.remote.Result
import com.sofascoreacademy.minisofa.data.remote.response.EventResponse
import com.sofascoreacademy.minisofa.data.remote.response.mapper.toCardIncident
import com.sofascoreacademy.minisofa.data.remote.response.mapper.toEvent
import com.sofascoreacademy.minisofa.data.remote.response.mapper.toGoalIncident
import com.sofascoreacademy.minisofa.data.remote.response.mapper.toPeriodIncident
import com.sofascoreacademy.minisofa.data.remote.response.mapper.toSport
import com.sofascoreacademy.minisofa.data.remote.response.mapper.toTournament
import com.sofascoreacademy.minisofa.data.remote.response.mapper.toTournamentStanding
import com.sofascoreacademy.minisofa.data.repository.MinisofaRepository
import com.sofascoreacademy.minisofa.data.repository.Resource
import com.sofascoreacademy.minisofa.ui.event_details_page.adapter.IncidentListItem
import com.sofascoreacademy.minisofa.ui.event_details_page.adapter.IncidentListItem.Companion.toIncidentItemList
import com.sofascoreacademy.minisofa.ui.home.main_list_page.adapter.EventListRecyclerAdapter.EventListItem
import com.sofascoreacademy.minisofa.ui.home.leagues_page.adapter.LeaguesRecyclerAdapter.LeagueItem
import com.sofascoreacademy.minisofa.ui.tournament_details_page.EventPagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate


const val PLUS_AND_MINUS_DAYS = 7


class MainViewModel(private val application: Application) : AndroidViewModel(application) {

    enum class Showing {
        EVENTS, LEAGUES
    }
    var state = Showing.EVENTS

    val sportIconMap = mapOf(
        1 to R.drawable.icon_football,
        2 to R.drawable.icon_basketball,
        3 to R.drawable.icon_american_football
    )

    private val _sportsLiveData = MutableLiveData<Resource<List<Sport>>>()
    val sportsLiveData: LiveData<Resource<List<Sport>>> = _sportsLiveData

    private val _eventsLiveData = MutableLiveData<Resource<List<EventListItem>>>()
    val eventsLiveData: LiveData<Resource<List<EventListItem>>> = _eventsLiveData

    private val _leaguesLiveData = MutableLiveData<Resource<List<LeagueItem>>>()
    val leaguesLiveData: LiveData<Resource<List<LeagueItem>>> = _leaguesLiveData

    private val _incidentsLiveData = MutableLiveData<Resource<List<IncidentListItem>>>()
    val incidentsLiveData: LiveData<Resource<List<IncidentListItem>>> = _incidentsLiveData

    private val _standingsLiveData = MutableLiveData<Resource<List<TournamentStanding>>>()
    val standingsLiveData: LiveData<Resource<List<TournamentStanding>>> = _standingsLiveData

    private lateinit var tournamentEventsResponseLiveData: LiveData<PagingData<EventResponse>>


    private var dateFormatPattern: String = getDateFormatPattern(DateFormat.DD_MM_YYYY)
    fun getDateFormatPattern() = dateFormatPattern

    private var dayAndMonthDateFormatPattern: String = getDayAndMonthDateFormatPattern(DateFormat.DD_MM_YYYY)
    fun getDayAndMonthDateFormatPattern() = dayAndMonthDateFormatPattern

    fun observeDateFormatPreference(lifeCycleScope: CoroutineScope) = lifeCycleScope.launch(Dispatchers.IO) {
        application.dateFormatFlow.collect {
            dateFormatPattern = getDateFormatPattern(DateFormat.entries[it])
            dayAndMonthDateFormatPattern = getDayAndMonthDateFormatPattern(DateFormat.entries[it])
        }
    }


    private val minisofaRepository = MinisofaRepository()



    val vpDateMap: Map<Int, String> = mutableMapOf<Int, String>().also {
        val today = LocalDate.now()
        val todayIndex = PLUS_AND_MINUS_DAYS
        it[todayIndex] = today.toString()
        for (i in 1 .. todayIndex) {
            it[todayIndex - i] = today.minusDays(i.toLong()).toString()
        }
        for (i in 1..todayIndex) {
            it[todayIndex + i] = today.plusDays(i.toLong()).toString()
        }
    }


    val currentDate = mutableMapOf<Int, String>()


    suspend fun fetchAllSports() {
        viewModelScope.launch(Dispatchers.IO) {
            _sportsLiveData.postValue(Resource.Loading())
            when (val sportsResult = minisofaRepository.fetchSports()) {
                is Result.Error -> {
                    _sportsLiveData.postValue(Resource.Failure(sportsResult.error))
                }
                is Result.Success -> {
                    val sports = withContext(Dispatchers.Default) {
                        sportsResult.data.map { sport ->
                            sport.toSport()
                        }.onEach { sport ->
                            currentDate[sport.id] = LocalDate.now().toString()
                        }
                    }
                    _sportsLiveData.postValue(Resource.Success(sports))
                }
            }
        }
    }

    suspend fun fetchEventsForSportAndDate(
        sportSlug: String, date: String,
        onTournamentClick: (Tournament) -> Unit,
        onEventClick: (Event) -> Unit
    ){
        viewModelScope.launch(Dispatchers.IO) {
            _eventsLiveData.postValue(Resource.Loading())
            when (val eventsResult = minisofaRepository.fetchEvents(sportSlug, date)) {
                is Result.Error -> {
                    _eventsLiveData.postValue(Resource.Failure(eventsResult.error))
                }
                is Result.Success -> {
                    val eventList = withContext(Dispatchers.Default) {
                        eventsResult.data.groupBy { event ->
                            event.tournament
                        }.flatMap { (tournamentResponse, eventResponseList) ->
                            listOf(
                                EventListItem.HeaderItem(
                                    tournamentResponse.toTournament(),
                                    onTournamentClick
                                ),
                                *(eventResponseList.map { eventResponse ->
                                    EventListItem.EventItem(
                                        eventResponse.toEvent(),
                                        onEventClick
                                    )
                                }).toTypedArray()
                            )
                        }
                    }
                    _eventsLiveData.postValue(Resource.Success(eventList))
                }
            }
        }
    }

    suspend fun fetchLeaguesForSport(sportSlug: String, onTournamentClick: (Tournament) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _leaguesLiveData.postValue(Resource.Loading())
            when (val leaguesResult = minisofaRepository.fetchTournaments(sportSlug)) {
                is Result.Error -> {
                    _leaguesLiveData.postValue(Resource.Failure(leaguesResult.error))
                }
                is Result.Success -> {
                    val leagues = withContext(Dispatchers.Default) {
                        leaguesResult.data.map { tournamentResponse ->
                            LeagueItem(
                                tournamentResponse.toTournament(),
                                onTournamentClick
                            )
                        }
                    }.sortedBy { it.tournament.name }
                    _leaguesLiveData.postValue(Resource.Success(leagues))
                }
            }
        }
    }

    suspend fun fetchIncidentsForEvent(eventId: Int, viewType: IncidentListItem.ViewType) {
        viewModelScope.launch(Dispatchers.IO) {
            _incidentsLiveData.postValue(Resource.Loading())
            when (val incidentsResult = minisofaRepository.fetchIncidents(eventId)) {
                is Result.Error -> {
                    _incidentsLiveData.postValue(Resource.Failure(incidentsResult.error))
                }
                is Result.Success -> {
                    val incidents = withContext(Dispatchers.Default) {
                        incidentsResult.data.reversed().map { incidentResponse ->
                            when (IncidentType.fromString(incidentResponse.type)) {
                                IncidentType.CARD -> incidentResponse.toCardIncident()
                                IncidentType.GOAL -> incidentResponse.toGoalIncident()
                                IncidentType.PERIOD -> incidentResponse.toPeriodIncident()
                            }
                        }.toIncidentItemList(viewType)
                    }
                    _incidentsLiveData.postValue(Resource.Success(incidents))
                }
            }
        }
    }

    suspend fun fetchTournamentStandings(tournamentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _standingsLiveData.postValue(Resource.Loading())
            when (val standingsResult = minisofaRepository.fetchTournamentStandings(tournamentId)) {
                is Result.Error -> {
                    _standingsLiveData.postValue(Resource.Failure(standingsResult.error))
                }
                is Result.Success -> {
                    val standings = withContext(Dispatchers.Default) {
                        listOf(createTournamentStandingHeader(standingsResult.data.tournament.sport.toSport())) +
                        standingsResult.data.sortedStandingsRows.map { tournamentStandingResponse ->
                            tournamentStandingResponse.toTournamentStanding(standingsResult.data)
                        }
                    }
                    _standingsLiveData.postValue(Resource.Success(standings))
                }
            }
        }
    }

    fun observePagedTournamentEventsLiveData(
        tournamentId: Int,
        onClick: (Event) -> Unit,
        lifecycleOwner: LifecycleOwner,
        submitData: suspend (PagingData<EventListItem.EventItem>) -> Unit
    ) {
        tournamentEventsResponseLiveData = Pager(
            PagingConfig(pageSize = ITEMS_PER_PAGE)
        ) {
            EventPagingSource(
                getLastEvents = { page -> minisofaRepository.fetchPastEventsForTournament(tournamentId, page) },
                getNextEvents = { page -> minisofaRepository.fetchFutureEventsForTournament(tournamentId, page) },
            )
        }.liveData

        tournamentEventsResponseLiveData.observe(lifecycleOwner) { pagingData ->
            viewModelScope.launch(Dispatchers.Default) {
                val eventList = pagingData.map { eventResponse ->
                    EventListItem.EventItem(
                        eventResponse.toEvent(),
                        onClick
                    )
                }
                withContext(Dispatchers.Main) {
                    submitData(eventList)
                }
            }
        }
    }

    fun removeObservers(lifecycleOwner: LifecycleOwner) {
        _sportsLiveData.removeObservers(lifecycleOwner)
        _eventsLiveData.removeObservers(lifecycleOwner)
        _incidentsLiveData.removeObservers(lifecycleOwner)
        _standingsLiveData.removeObservers(lifecycleOwner)
        tournamentEventsResponseLiveData.removeObservers(lifecycleOwner)
    }
}