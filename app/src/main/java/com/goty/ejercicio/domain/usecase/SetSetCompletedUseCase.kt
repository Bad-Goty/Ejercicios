package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.repository.WorkoutLogRepository
import javax.inject.Inject

/** Guarda (o quita) una serie completada hoy en el registro de entrenamiento. */
class SetSetCompletedUseCase @Inject constructor(
    private val repository: WorkoutLogRepository,
) {
    suspend operator fun invoke(exerciseId: Long, setNumber: Int, reps: Int, completed: Boolean) =
        repository.setCompleted(exerciseId, setNumber, reps, completed)
}
