package com.goty.ejercicio.domain.repository

import kotlinx.coroutines.flow.Flow

/** Registro de entrenamiento: qué series se completaron cada día. */
interface WorkoutLogRepository {

    /** Series completadas hoy, agrupadas por ejercicio: id del ejercicio -> números de serie. */
    fun observeTodayCompletedSets(): Flow<Map<Long, Set<Int>>>

    /** Marca (o desmarca) una serie de hoy como completada. */
    suspend fun setCompleted(exerciseId: Long, setNumber: Int, reps: Int, completed: Boolean)
}
