package com.sofascoreacademy.minisofascore.data.local.entity.enums

enum class EventWinnerCode {

    HOME, AWAY, DRAW;

    companion object {
        fun fromString(value: String): EventWinnerCode {
            return when (value) {
                "home" -> HOME
                "away" -> AWAY
                "draw" -> DRAW
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}
