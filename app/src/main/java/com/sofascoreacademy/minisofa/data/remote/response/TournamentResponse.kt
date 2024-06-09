package com.sofascoreacademy.minisofa.data.remote.response

data class TournamentResponse(
    val id: Int,
    val name: String,
    val slug: String,
    val sport: SportResponse,
    val country: CountryResponse
)
