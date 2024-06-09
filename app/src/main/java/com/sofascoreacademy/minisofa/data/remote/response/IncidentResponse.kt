package com.sofascoreacademy.minisofa.data.remote.response

// https://academy-backend.sofascore.dev/api/docs#tag/Event/operation/get_app_event_incidents
data class IncidentResponse(
    val type: String,
    val id: Int,

    val player: PlayerResponse?,
    val teamSide: String?,
    val color: String?,
    val time: Int?,

    val scoringTeam: String?,
    val homeScore: Int?,
    val awayScore: Int?,
    val goalType: String?,

    val text: String?
)
