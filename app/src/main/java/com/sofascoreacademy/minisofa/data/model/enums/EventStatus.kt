package com.sofascoreacademy.minisofa.data.model.enums

enum class EventStatus {

    NOT_STARTED, IN_PROGRESS, FINISHED;

    companion object {
        fun fromString(value: String): EventStatus {
            return when (value) {
                "notstarted" -> NOT_STARTED
                "inprogress" -> IN_PROGRESS
                "finished" -> FINISHED
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}
