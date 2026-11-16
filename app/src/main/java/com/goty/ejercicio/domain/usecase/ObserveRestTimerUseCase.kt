package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.model.RestTimer
import com.goty.ejercicio.domain.repository.RestTimerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Descanso guardado (sigue aunque se haya cerrado la app). */
class ObserveRestTimerUseCase @Inject constructor(
    private val repository: RestTimerRepository,
) {
    operator fun invoke(): Flow<RestTimer?> = repository.restTimer
}
