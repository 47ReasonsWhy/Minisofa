package com.sofascoreacademy.minisofa.data.model.enums

enum class IncidentType {
    CARD, GOAL, PERIOD;

    companion object {
        fun fromString(value: String): IncidentType {
            return when (value) {
                "card" -> CARD
                "goal" -> GOAL
                "period" -> PERIOD
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}
