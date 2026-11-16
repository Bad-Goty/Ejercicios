package com.goty.ejercicio.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.goty.ejercicio.domain.model.RestTimer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/** Guarda en DataStore el descanso en curso (cuándo termina y cuánto dura). */
@Singleton
class RestTimerDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val restTimer: Flow<RestTimer?> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { preferences ->
            val endAt = preferences[REST_END_AT]
            val total = preferences[REST_TOTAL_SECONDS]
            if (endAt != null && total != null) RestTimer(endAt, total) else null
        }

    suspend fun save(timer: RestTimer) {
        dataStore.edit { preferences ->
            preferences[REST_END_AT] = timer.endAtMillis
            preferences[REST_TOTAL_SECONDS] = timer.totalSeconds
        }
    }

    /** Borra el descanso solo si es el que termina en [endAtMillis]. Devuelve si lo borró. */
    suspend fun clearIfEndsAt(endAtMillis: Long): Boolean {
        var cleared = false
        dataStore.edit { preferences ->
            if (preferences[REST_END_AT] == endAtMillis) {
                preferences.remove(REST_END_AT)
                preferences.remove(REST_TOTAL_SECONDS)
                cleared = true
            }
        }
        return cleared
    }

    private companion object {
        val REST_END_AT = longPreferencesKey("rest_end_at")
        val REST_TOTAL_SECONDS = intPreferencesKey("rest_total_seconds")
    }
}
