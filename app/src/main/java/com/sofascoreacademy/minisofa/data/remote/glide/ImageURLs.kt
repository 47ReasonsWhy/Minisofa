package com.sofascoreacademy.minisofa.data.remote.glide

import com.sofascoreacademy.minisofa.data.remote.BASE_URL

fun getTournamentLogoURL(id: Int): String {
    return BASE_URL + "tournament/$id/image"
}

fun getTeamLogoURL(id: Int): String {
    return BASE_URL + "team/$id/image"
}