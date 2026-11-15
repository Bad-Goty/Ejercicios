package com.goty.ejercicio.data.repository

import com.goty.ejercicio.data.local.dao.CategoriaDao
import com.goty.ejercicio.data.mapper.toDomain
import com.goty.ejercicio.domain.model.MuscleCategory
import com.goty.ejercicio.domain.repository.MuscleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MuscleRepositoryImpl @Inject constructor(
    private val categoriaDao: CategoriaDao,
) : MuscleRepository {

    override fun getCategoriesWithMuscles(): Flow<List<MuscleCategory>> =
        categoriaDao.obtenerConMusculos().map { list -> list.map { it.toDomain() } }
}
