package com.sofascoreacademy.minisofascore.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.sofascoreacademy.minisofascore.data.local.entity.enums.EventStatus
import com.sofascoreacademy.minisofascore.data.local.entity.enums.EventWinnerCode
import java.text.SimpleDateFormat
import java.util.Date

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = Team::class,
            parentColumns = ["id"],
            childColumns = ["homeTeamId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = Team::class,
            parentColumns = ["id"],
            childColumns = ["awayTeamId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = Tournament::class,
            parentColumns = ["id"],
            childColumns = ["tournamentId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        )
    ],
    indices = [
        androidx.room.Index(value = ["homeTeamId"]),
        androidx.room.Index(value = ["awayTeamId"]),
        androidx.room.Index(value = ["tournamentId"])
    ]
)
data class Event(
    @PrimaryKey val id: Int,
    val slug: String,
    val tournamentId: Int,
    val homeTeamId: Int,
    val awayTeamId: Int,
    val status: EventStatus,
    val startDate: String?,
    val startTime: String?,
    @Embedded(prefix = "home_score_") val homeScore: Score,
    @Embedded(prefix = "away_score_") val awayScore: Score,
    val winnerCode: EventWinnerCode?,
    val round: Int
)

data class Score(
    val total: Int,
    val period1: Int,
    val period2: Int,
    val period3: Int,
    val period4: Int,
    val overtime: Int
)

data class EventWithTeamsAndTournament(
    @Embedded val event: Event,
    @Relation(
        parentColumn = "homeTeamId",
        entityColumn = "id"
    )
    val homeTeams: List<Team>,
    @Relation(
        parentColumn = "awayTeamId",
        entityColumn = "id"
    )
    val awayTeams: List<Team>,
    @Relation(
        parentColumn = "tournamentId",
        entityColumn = "id"
    )
    val tournaments: List<Tournament>
)
