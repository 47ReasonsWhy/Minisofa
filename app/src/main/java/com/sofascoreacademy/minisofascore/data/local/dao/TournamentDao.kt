package com.sofascoreacademy.minisofascore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.sofascoreacademy.minisofascore.data.local.entity.Tournament

@Dao
interface TournamentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournaments(teams: List<Tournament>)
}