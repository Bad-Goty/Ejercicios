package com.goty.ejercicio.data.repository

import com.goty.ejercicio.data.local.dao.ExerciseDao
import com.goty.ejercicio.data.mapper.toDomain
import com.goty.ejercicio.data.mapper.toEntity
import com.goty.ejercicio.domain.model.Exercise
import com.goty.ejercicio.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val dao: ExerciseDao,
) : ExerciseRepository {

    override fun getExercises(): Flow<List<Exercise>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun addExercise(exercise: Exercise) {
        dao.insert(exercise.toEntity())
    }
}
