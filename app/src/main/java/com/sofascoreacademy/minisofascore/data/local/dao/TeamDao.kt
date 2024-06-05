package com.sofascoreacademy.minisofascore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sofascoreacademy.minisofascore.data.local.entity.Event
import com.sofascoreacademy.minisofascore.data.local.entity.Team
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeams(teams: List<Team>)
}