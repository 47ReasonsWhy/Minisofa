package com.sofascoreacademy.minisofa.data.remote.response

data class PlayerResponse(
    val id: Int,
    val name: String,
    val slug: String,
    val country: CountryResponse,
    val position: String
)
