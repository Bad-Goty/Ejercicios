package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.model.Exercise
import com.goty.ejercicio.domain.model.todayDayOfWeek
import com.goty.ejercicio.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Ejercicios de las categorías que tocan hoy en la rutina (lo que se muestra en Inicio). */
class GetTodayExercisesUseCase @Inject constructor(
    private val repository: ExerciseRepository,
) {
    operator fun invoke(): Flow<List<Exercise>> = repository.getExercisesForDay(todayDayOfWeek())
}
