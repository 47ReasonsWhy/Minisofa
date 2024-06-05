package com.sofascoreacademy.minisofascore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "sports")
data class Sport(
    @PrimaryKey val id: Int,
    val name: String,
    val slug: String
) : Serializable
