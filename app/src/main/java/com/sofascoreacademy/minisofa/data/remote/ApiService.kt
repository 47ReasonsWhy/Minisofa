package com.sofascoreacademy.minisofa.data.remote

import com.sofascoreacademy.minisofa.data.remote.response.EventResponse
import com.sofascoreacademy.minisofa.data.remote.response.IncidentResponse
import com.sofascoreacademy.minisofa.data.remote.response.SportResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("sports")
    suspend fun getSports(): List<SportResponse>

    @GET("sport/{slug}/events/{date}")
    suspend fun getEvents(
        @Path("slug") slug: String,
        @Path("date") date: String
    ): List<EventResponse>

    @GET("event/{id}/incidents")
    suspend fun getIncidents(
        @Path("id") id: Int
    ): List<IncidentResponse>
}