package com.sofascoreacademy.minisofa.data.model.enum

enum class StandingsType {
    TOTAL, HOME, AWAY;

    companion object {
        fun fromString(value: String): StandingsType {
            return when (value) {
                "total" -> TOTAL
                "home" -> HOME
                "away" -> AWAY
                else -> throw IllegalArgumentException("Invalid tournament standings type")
            }
        }
    }
}