package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.repository.ExerciseImageRepository
import com.goty.ejercicio.domain.repository.ExerciseRepository
import javax.inject.Inject

/**
 * Guarda un ejercicio nuevo con sus series y repeticiones.
 * - El nombre es obligatorio (se guarda sin espacios sobrantes).
 * - Series y repeticiones deben ser al menos 1.
 * - Si hay imagen, primero se copia al almacenamiento de la app y se guarda su ruta.
 * Devuelve el id generado.
 */
class SaveExerciseUseCase @Inject constructor(
    private val repository: ExerciseRepository,
    private val imageRepository: ExerciseImageRepository,
) {
    suspend operator fun invoke(
        name: String,
        series: Int,
        reps: Int,
        mainMuscleId: Long?,
        imageUri: String? = null,
    ): Long {
        val cleanName = name.trim()
        require(cleanName.isNotEmpty()) { "El nombre del ejercicio es obligatorio" }
        require(series >= 1) { "Debe tener al menos 1 serie" }
        require(reps >= 1) { "Debe tener al menos 1 repetición" }

        val photoPath = imageUri?.let { imageRepository.importImage(it) }
        return try {
            repository.saveExercise(cleanName, series, reps, mainMuscleId, photoPath)
        } catch (e: Exception) {
            // Si no se pudo guardar el ejercicio, no dejamos la imagen copiada huérfana
            photoPath?.let { imageRepository.deleteImage(it) }
            throw e
        }
    }
}
