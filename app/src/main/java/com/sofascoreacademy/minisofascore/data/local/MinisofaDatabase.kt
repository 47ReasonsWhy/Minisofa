package com.sofascoreacademy.minisofascore.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sofascoreacademy.minisofascore.data.local.dao.CountryDao
import com.sofascoreacademy.minisofascore.data.local.dao.EventDao
import com.sofascoreacademy.minisofascore.data.local.dao.SportDao
import com.sofascoreacademy.minisofascore.data.local.dao.TeamDao
import com.sofascoreacademy.minisofascore.data.local.dao.TournamentDao
import com.sofascoreacademy.minisofascore.data.local.entity.Country
import com.sofascoreacademy.minisofascore.data.local.entity.Event
import com.sofascoreacademy.minisofascore.data.local.entity.Sport
import com.sofascoreacademy.minisofascore.data.local.entity.Team
import com.sofascoreacademy.minisofascore.data.local.entity.Tournament

@Database(
    entities = [Country::class, Event::class, Sport::class, Team::class, Tournament::class],
    version = 1,
    exportSchema = false
)
abstract class MinisofaDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun sportDao(): SportDao
    abstract fun tournamentDao(): TournamentDao
    abstract fun teamDao(): TeamDao
    abstract fun countryDao(): CountryDao

    companion object {
        private const val DATABASE_NAME = "minisofa.db"

        @Volatile
        private var INSTANCE: MinisofaDatabase? = null

        fun getInstance(context: Context): MinisofaDatabase {
            if (INSTANCE == null) {
                synchronized(MinisofaDatabase::class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            MinisofaDatabase::class.java,
                            DATABASE_NAME
                        ).fallbackToDestructiveMigration().build()
                    }
                }
            }
            return INSTANCE!!
        }
    }
}