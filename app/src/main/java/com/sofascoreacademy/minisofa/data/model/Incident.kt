package com.sofascoreacademy.minisofa.data.model

import com.sofascoreacademy.minisofa.data.model.enum.CardColor
import com.sofascoreacademy.minisofa.data.model.enum.GoalType
import com.sofascoreacademy.minisofa.data.model.enum.IncidentType
import com.sofascoreacademy.minisofa.data.model.enum.TeamSide

sealed class Incident {

    abstract val type: IncidentType

    data class Card(
        val id: Int,
        val player: Player,
        val teamSide: TeamSide,
        val color: CardColor,
        val time: Int,
    ) : Incident() {
        override val type = IncidentType.CARD
    }

    data class Goal(
        val id: Int,
        val player: Player,
        val scoringTeam: TeamSide,
        val homeScore: Int,
        val awayScore: Int,
        val goalType: GoalType,
        val time: Int
    ) : Incident() {
        override val type = IncidentType.GOAL
    }

    data class Period(
        val id: Int,
        val text: String,
        val time: Int
    ) : Incident() {
        override val type = IncidentType.PERIOD
    }

}
