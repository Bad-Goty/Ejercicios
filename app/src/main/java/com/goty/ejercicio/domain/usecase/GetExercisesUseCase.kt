package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.model.Exercise
import com.goty.ejercicio.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository,
) {
    operator fun invoke(): Flow<List<Exercise>> = repository.getExercises()
}
