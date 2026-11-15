package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.repository.StepsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStepGoalUseCase @Inject constructor(
    private val repository: StepsRepository,
) {
    operator fun invoke(): Flow<Int> = repository.stepGoal
}
