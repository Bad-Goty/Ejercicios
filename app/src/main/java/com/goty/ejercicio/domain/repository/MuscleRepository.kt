package com.goty.ejercicio.domain.repository

import com.goty.ejercicio.domain.model.MuscleCategory
import kotlinx.coroutines.flow.Flow

/** Contrato del repositorio de músculos. La capa data lo implementa. */
interface MuscleRepository {
    fun getCategoriesWithMuscles(): Flow<List<MuscleCategory>>
}
