package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.repository.StepsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTodayStepsUseCase @Inject constructor(
    private val repository: StepsRepository,
) {
    val isStepSensorAvailable: Boolean
        get() = repository.isStepSensorAvailable

    /** @param trackWithSensor true si hay permiso para contar pasos con el sensor. */
    operator fun invoke(trackWithSensor: Boolean): Flow<Int> =
        repository.observeTodaySteps(trackWithSensor && repository.isStepSensorAvailable)
}
