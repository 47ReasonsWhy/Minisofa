package com.sofascoreacademy.minisofascore.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sofascoreacademy.minisofascore.data.local.entity.Sport
import com.sofascoreacademy.minisofascore.data.repository.MinisofaRepository
import com.sofascoreacademy.minisofascore.data.repository.Resource
import com.sofascoreacademy.minisofascore.ui.home.adapter.EventsForSportAndDateRecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _sports = MutableLiveData<Resource<List<Sport>>>()
    val sports: LiveData<Resource<List<Sport>>> = _sports

    private val _events = MutableLiveData<Resource<List<EventsForSportAndDateRecyclerAdapter.EventListItem>>>()
    val events: LiveData<Resource<List<EventsForSportAndDateRecyclerAdapter.EventListItem>>> = _events

    private val minisofaRepository = MinisofaRepository(application)



    fun fetchSports() {
        viewModelScope.launch {
            minisofaRepository.fetchSports().collect {
                _sports.postValue(it)
            }
        }
    }

    fun fetchEventsForSport(sportId: Int, sportSlug: String, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            minisofaRepository.fetchEvents(sportId, sportSlug, date).collect {
                when (it) {
                    is Resource.Loading -> {
                        withContext(Dispatchers.Main) { _events.postValue(Resource.Loading()) }
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
}