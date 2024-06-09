package com.sofascoreacademy.minisofa.data.model

import com.sofascoreacademy.minisofa.data.model.enum.EventStatus
import com.sofascoreacademy.minisofa.data.model.enum.EventWinnerCode
import java.io.Serializable

data class Event(
    val id: Int,
    val slug: String,
    val tournament: Tournament,
    val homeTeam: Team,
    val awayTeam: Team,
    val status: EventStatus,
    val startDate: String?,
    val startTime: String?,
    val homeScore: Score,
    val awayScore: Score,
    val winnerCode: EventWinnerCode?,
    val round: Int
) : Serializable

data class Score(
    val total: Int,
    val period1: Int,
    val period2: Int,
    val period3: Int,
    val period4: Int,
    val overtime: Int
) : Serializable
