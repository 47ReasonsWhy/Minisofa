package com.sofascoreacademy.minisofascore.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "tournaments",
    foreignKeys = [
        ForeignKey(
            entity = Sport::class,
            parentColumns = ["id"],
            childColumns = ["sportId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        ),
        ForeignKey(
            entity = Country::class,
            parentColumns = ["id"],
            childColumns = ["countryId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
            deferred = true
        )
    ],
    indices = [
        Index(value = ["sportId"]),
        Index(value = ["countryId"])
    ]
)
data class Tournament(
    @PrimaryKey val id: Int,
    val name: String,
    val slug: String,
    val sportId: Int,
    val countryId: Int
)

data class TournamentWithSportAndCountry(
    @Embedded val tournament: Tournament,
    @Relation(
        parentColumn = "sportId",
        entityColumn = "id"
    )
    val sports: List<Sport>,
    @Relation(
        parentColumn = "countryId",
        entityColumn = "id"
    )
    val countries: List<Country>
)
