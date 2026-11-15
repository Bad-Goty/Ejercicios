package com.goty.ejercicio.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/** Guarda en DataStore los pasos del día y la meta de pasos. */
@Singleton
class StepsDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private val data: Flow<Preferences> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }

    /** Pasos de hoy. Si lo guardado es de otro día, hoy va en 0. */
    val todaySteps: Flow<Int> = data.map { preferences ->
        if (preferences[STEPS_DATE] == todayKey()) preferences[STEPS_TODAY] ?: 0 else 0
    }

    val stepGoal: Flow<Int> = data.map { preferences ->
        preferences[STEP_GOAL] ?: DEFAULT_STEP_GOAL
    }

    suspend fun setStepGoal(goal: Int) {
        dataStore.edit { preferences -> preferences[STEP_GOAL] = goal }
    }

    /**
     * Registra una lectura del sensor (total desde el encendido) y suma a hoy
     * los pasos dados desde la lectura anterior.
     */
    suspend fun recordSensorReading(totalSinceBoot: Int) {
        dataStore.edit { preferences ->
            val today = todayKey()
            val lastTotal = preferences[LAST_SENSOR_TOTAL]

            val newSteps = when {
                lastTotal == null -> 0                       // primera lectura: empieza a contar desde aquí
                totalSinceBoot >= lastTotal -> totalSinceBoot - lastTotal
                else -> totalSinceBoot                        // el teléfono se reinició: el sensor volvió a 0
            }

            if (preferences[STEPS_DATE] != today) {
                // Día nuevo: reinicia el conteo
                preferences[STEPS_DATE] = today
                preferences[STEPS_TODAY] = 0
            } else {
                preferences[STEPS_TODAY] = (preferences[STEPS_TODAY] ?: 0) + newSteps
            }
            preferences[LAST_SENSOR_TOTAL] = totalSinceBoot
        }
    }

    private fun todayKey(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private companion object {
        const val DEFAULT_STEP_GOAL = 1000

        val STEPS_DATE = stringPreferencesKey("steps_date")
        val STEPS_TODAY = intPreferencesKey("steps_today")
        val LAST_SENSOR_TOTAL = intPreferencesKey("steps_last_sensor_total")
        val STEP_GOAL = intPreferencesKey("step_goal")
    }
}
