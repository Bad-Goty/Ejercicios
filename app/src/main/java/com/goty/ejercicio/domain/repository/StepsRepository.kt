package com.goty.ejercicio.domain.repository

import kotlinx.coroutines.flow.Flow

interface StepsRepository {

    /** false si el dispositivo no tiene sensor de pasos. */
    val isStepSensorAvailable: Boolean

    /**
     * Pasos de hoy. Si [trackWithSensor] es true, además cuenta los pasos nuevos
     * con el sensor mientras se observe el Flow.
     */
    fun observeTodaySteps(trackWithSensor: Boolean): Flow<Int>

    /**
     * Lee el sensor una vez y guarda los pasos nuevos (usado en segundo plano).
     * @return true si se pudo leer el sensor.
     */
    suspend fun syncStepsFromSensor(): Boolean

    val stepGoal: Flow<Int>

    suspend fun setStepGoal(goal: Int)
}
