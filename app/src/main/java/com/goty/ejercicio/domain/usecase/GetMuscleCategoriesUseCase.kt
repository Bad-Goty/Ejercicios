package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.model.MuscleCategory
import com.goty.ejercicio.domain.repository.MuscleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Devuelve las categorías con sus músculos para elegir en "Agregar ejercicio". */
class GetMuscleCategoriesUseCase @Inject constructor(
    private val repository: MuscleRepository,
) {
    operator fun invoke(): Flow<List<MuscleCategory>> = repository.getCategoriesWithMuscles()
}
