package com.goty.ejercicio.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sensor de pasos del teléfono (TYPE_STEP_COUNTER).
 *
 * El sensor entrega el total de pasos desde que se encendió el teléfono,
 * no los pasos del día. El cálculo diario lo hace StepsDataSource.
 */
@Singleton
class StepCounterSensor @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val sensorManager = context.getSystemService(SensorManager::class.java)
    private val sensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    /** false en dispositivos sin sensor de pasos (p. ej. la mayoría de emuladores). */
    val isAvailable: Boolean get() = sensor != null

    /** Emite el total de pasos desde el encendido mientras alguien lo esté observando. */
    fun totalStepsSinceBoot(): Flow<Int> {
        val manager = sensorManager
        val stepSensor = sensor
        if (manager == null || stepSensor == null) return emptyFlow()

        return callbackFlow {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    trySend(event.values[0].toInt())
                }

                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
            }
            manager.registerListener(listener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
            awaitClose { manager.unregisterListener(listener) }
        }
    }

    /**
     * Lee el total actual una sola vez (para el trabajo en segundo plano).
     * Devuelve null si no hay sensor o no responde a tiempo.
     */
    suspend fun readTotalStepsSinceBoot(timeoutMillis: Long = 10_000): Int? =
        withTimeoutOrNull(timeoutMillis) { totalStepsSinceBoot().firstOrNull() }
}
