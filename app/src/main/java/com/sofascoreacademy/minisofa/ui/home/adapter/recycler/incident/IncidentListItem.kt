package com.sofascoreacademy.minisofa.ui.home.adapter.recycler.incident

import com.sofascoreacademy.minisofa.data.model.Event
import com.sofascoreacademy.minisofa.data.model.Incident

sealed class IncidentListItem {
    data class IncidentItemPeriod(val text: String) : IncidentListItem()
    data class IncidentItemFootball(val incident: Incident) : IncidentListItem()
    data class IncidentItemBasketball(val incident: Incident) : IncidentListItem()

    enum class ViewType {
        PERIOD,
        FOOTBALL,
        BASKETBALL
    }

    companion object {

        /**
         * Couldn't find a way to sooner or later not "hardcode" the sport types
         */
        fun Event.getIncidentViewType(): ViewType {
            return when (tournament.sport.id) {
                1 -> ViewType.FOOTBALL
                2 -> ViewType.BASKETBALL
                3 -> ViewType.FOOTBALL
                else -> throw IllegalArgumentException("Unknown sport")
            }
        }

        fun List<Incident>.toIncidentItemList(viewType: ViewType): List<IncidentListItem> {
            return map {
                if (it is Incident.Period) IncidentItemPeriod(it.text)
                else when (viewType) {
                    ViewType.FOOTBALL -> IncidentItemFootball(it)
                    ViewType.BASKETBALL -> IncidentItemBasketball(it)
                    else -> throw IllegalArgumentException("Unknown sport")
                }
            }
        }

    }
}
