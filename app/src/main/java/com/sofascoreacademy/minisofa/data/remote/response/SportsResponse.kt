package com.sofascoreacademy.minisofa.data.remote.response

import java.io.Serializable

// https://academy-backend.sofascore.dev/api/docs#tag/Sport/operation/get_app_sports
data class SportResponse(
    val id: Int,
    val name: String,
    val slug: String
) : Serializable