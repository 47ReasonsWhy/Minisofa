package com.sofascoreacademy.minisofa.data.model.enums

enum class TeamSide {
    HOME, AWAY;

    companion object {
        fun fromString(value: String): TeamSide {
            return when (value) {
                "home" -> HOME
                "away" -> AWAY
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}