package com.goty.ejercicio.domain.repository

import com.goty.ejercicio.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

/** Contrato del repositorio. La capa data lo implementa. */
interface ExerciseRepository {
    fun getExercises(): Flow<List<Exercise>>
    suspend fun addExercise(exercise: Exercise)

    /**
     * Guarda un ejercicio con su músculo principal y la ruta de su foto (ambos opcionales).
     * Devuelve el id generado.
     */
    suspend fun saveExercise(name: String, mainMuscleId: Long?, photoPath: String?): Long
}
