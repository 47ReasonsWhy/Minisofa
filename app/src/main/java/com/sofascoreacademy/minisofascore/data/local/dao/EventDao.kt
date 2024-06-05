package com.sofascoreacademy.minisofascore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sofascoreacademy.minisofascore.data.local.entity.Event
import com.sofascoreacademy.minisofascore.data.local.entity.EventWithTeamsAndTournament
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Transaction
    @Query("SELECT * FROM events WHERE startDate = :startDate AND " +
            " tournamentId IN (SELECT id FROM tournaments WHERE sportId = :sportId)")
    fun getEventsWithTeamsAndTournamentBySportIdAndDate(sportId: Int, startDate: String): Flow<List<EventWithTeamsAndTournament>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<Event>)
}