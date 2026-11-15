package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.repository.StepsRepository
import javax.inject.Inject

class SetStepGoalUseCase @Inject constructor(
    private val repository: StepsRepository,
) {
    /** Guarda la meta solo si es válida (mayor a 0). */
    suspend operator fun invoke(goal: Int) {
        if (goal > 0) repository.setStepGoal(goal)
    }
}
