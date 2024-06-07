package com.sofascoreacademy.minisofa.data.remote.response

data class TeamResponse(
    val id: Int,
    val name: String,
    val country: CountryResponse
)
