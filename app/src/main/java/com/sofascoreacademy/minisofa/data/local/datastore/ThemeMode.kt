package com.sofascoreacademy.minisofa.data.local.datastore

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

val KEY_THEME_MODE = intPreferencesKey("theme_mode")

val Context.themeModeFlow: Flow<Int>
    get() = dataStore.data
        .catch {exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_THEME_MODE] ?: ThemeMode.SYSTEM.ordinal
        }

suspend fun Context.getThemeModePreference(): ThemeMode =
    ThemeMode.entries[themeModeFlow.first()]

suspend fun Context.setThemeModePreference(themeMode: ThemeMode) =
    dataStore.edit { preferences ->
        preferences[KEY_THEME_MODE] = themeMode.ordinal
    }

suspend fun Context.setThemeModeBasedOnPreference() {
    val themeMode = getThemeModePreference()
    AppCompatDelegate.setDefaultNightMode(
        when (themeMode) {
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    )
}
