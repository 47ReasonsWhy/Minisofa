package com.sofascoreacademy.minisofa.data.model.enum

import java.io.Serializable

enum class EventWinnerCode : Serializable {

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
