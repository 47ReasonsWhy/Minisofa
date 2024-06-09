package com.sofascoreacademy.minisofa.data.model.enums

enum class GoalType {
    REGULAR, OWN_GOAL, PENALTY,
    ONE_POINT, TWO_POINT, THREE_POINT,
    TOUCHDOWN, SAFETY, FIELD_GOAL,
    EXTRA_POINT;

    companion object {
        fun fromString(value: String): GoalType {
            return when (value) {
                "regular" -> REGULAR
                "owngoal" -> OWN_GOAL
                "penalty" -> PENALTY

                "onepoint" -> ONE_POINT
                "twopoint" -> TWO_POINT
                "threepoint" -> THREE_POINT

                "touchdown" -> TOUCHDOWN
                "safety" -> SAFETY
                "fieldgoal" -> FIELD_GOAL
                "extrapoint" -> EXTRA_POINT
                else -> throw IllegalArgumentException("Unknown value: $value")
            }
        }
    }
}