package com.goty.ejercicio.data.repository

import com.goty.ejercicio.data.local.dao.EjercicioDao
import com.goty.ejercicio.data.local.entity.Ejercicio
import com.goty.ejercicio.data.mapper.toDomain
import com.goty.ejercicio.data.mapper.toEntity
import com.goty.ejercicio.domain.model.Exercise
import com.goty.ejercicio.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val dao: EjercicioDao,
) : ExerciseRepository {

    override fun getExercises(): Flow<List<Exercise>> =
        dao.obtenerTodosConMusculoPrincipal().map { list -> list.map { it.toDomain() } }

    override fun getExercisesForDay(dayOfWeek: Int): Flow<List<Exercise>> =
        dao.obtenerDelDiaConMusculoPrincipal(dayOfWeek).map { list -> list.map { it.toDomain() } }

    override suspend fun addExercise(exercise: Exercise) {
        dao.insertar(exercise.toEntity())
    }

    override suspend fun saveExercise(
        name: String,
        series: Int,
        reps: Int,
        mainMuscleId: Long?,
        photoPath: String?,
    ): Long = dao.insertarConMusculoPrincipal(
        Ejercicio(nombre = name, series = series, repeticiones = reps, foto = photoPath),
        mainMuscleId,
    )
}
