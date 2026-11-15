package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.repository.ExerciseImageRepository
import javax.inject.Inject

/** Revisa que la foto/GIF elegida no pase de 10 MB. */
class ValidateExerciseImageUseCase @Inject constructor(
    private val imageRepository: ExerciseImageRepository,
) {
    suspend operator fun invoke(uri: String): Boolean =
        imageRepository.isWithinSize(uri, MAX_IMAGE_BYTES)

    companion object {
        const val MAX_IMAGE_BYTES = 10L * 1024 * 1024 // 10 MB
    }
}
