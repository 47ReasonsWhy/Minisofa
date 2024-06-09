package com.sofascoreacademy.minisofa.data.remote.response

data class ScoreResponse(
    val total: Int,
    val period1: Int,
    val period2: Int,
    val period3: Int,
    val period4: Int,
    val overtime: Int
)
