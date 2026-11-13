package com.goty.ejercicio.domain.repository

import com.goty.ejercicio.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

/** Contrato del repositorio. La capa data lo implementa. */
interface ExerciseRepository {
    fun getExercises(): Flow<List<Exercise>>
    suspend fun addExercise(exercise: Exercise)
}
