package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.repository.WorkoutLogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Series completadas hoy: id del ejercicio -> números de serie hechos. */
class ObserveTodayCompletedSetsUseCase @Inject constructor(
    private val repository: WorkoutLogRepository,
) {
    operator fun invoke(): Flow<Map<Long, Set<Int>>> = repository.observeTodayCompletedSets()
}
