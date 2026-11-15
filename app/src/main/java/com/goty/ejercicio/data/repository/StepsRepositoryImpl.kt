package com.goty.ejercicio.data.repository

import com.goty.ejercicio.data.preferences.StepsDataSource
import com.goty.ejercicio.data.sensor.StepCounterSensor
import com.goty.ejercicio.domain.repository.StepsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class StepsRepositoryImpl @Inject constructor(
    private val sensor: StepCounterSensor,
    private val dataSource: StepsDataSource,
) : StepsRepository {

    override val isStepSensorAvailable: Boolean
        get() = sensor.isAvailable

    override fun observeTodaySteps(trackWithSensor: Boolean): Flow<Int> {
        if (!trackWithSensor) return dataSource.todaySteps

        return channelFlow {
            // Mientras alguien observe: cada lectura del sensor se guarda en DataStore...
            launch {
                sensor.totalStepsSinceBoot().collect { total ->
                    dataSource.recordSensorReading(total)
                }
            }
            // ...y se emite lo guardado (DataStore es la única fuente de verdad)
            dataSource.todaySteps.collect { send(it) }
        }
    }

    override suspend fun syncStepsFromSensor(): Boolean {
        val total = sensor.readTotalStepsSinceBoot() ?: return false
        dataSource.recordSensorReading(total)
        return true
    }

    override val stepGoal: Flow<Int> = dataSource.stepGoal

    override suspend fun setStepGoal(goal: Int) {
        dataSource.setStepGoal(goal)
    }
}
