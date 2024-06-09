package com.sofascoreacademy.minisofa.data.model.enum

enum class CardColor {
    YELLOW, YELLOW_RED, RED;

    companion object {
        fun fromString(value: String): CardColor {
            return when (value) {
                "yellow" -> YELLOW
                "yellowred" -> YELLOW_RED
                "red" -> RED
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}