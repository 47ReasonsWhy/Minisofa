package com.sofascoreacademy.minisofa.data.local.datastore

import android.content.Context
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

enum class DateFormat {
    DD_MM_YYYY, MM_DD_YYYY, YYYY_MM_DD
}

val KEY_DATE_FORMAT = intPreferencesKey("date_format")

val Context.dateFormatFlow: Flow<Int>
    get() = dataStore.data
        .catch {exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_DATE_FORMAT] ?: DateFormat.DD_MM_YYYY.ordinal
        }

suspend fun Context.getDateFormatPreference(): DateFormat =
    DateFormat.entries[dateFormatFlow.first()]

suspend fun Context.setDateFormatPreference(dateFormat: DateFormat) =
    dataStore.edit { preferences ->
        preferences[KEY_DATE_FORMAT] = dateFormat.ordinal
    }

fun getDateFormatPattern(dateFormat: DateFormat): String {
    return when (dateFormat) {
        DateFormat.DD_MM_YYYY -> "dd.MM.yy"
        DateFormat.MM_DD_YYYY -> "MM.dd.yy"
        DateFormat.YYYY_MM_DD -> "yy.MM.dd"
    }
}

fun getDayAndMonthDateFormatPattern(dateFormat: DateFormat): String {
    return when (dateFormat) {
        DateFormat.DD_MM_YYYY -> "dd.MM."
        DateFormat.MM_DD_YYYY,
        DateFormat.YYYY_MM_DD -> "MM.dd."
    }
}
