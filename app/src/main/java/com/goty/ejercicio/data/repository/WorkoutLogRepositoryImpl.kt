package com.goty.ejercicio.data.repository

import com.goty.ejercicio.data.local.dao.SerieCompletadaDao
import com.goty.ejercicio.data.local.entity.SerieCompletada
import com.goty.ejercicio.domain.repository.WorkoutLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class WorkoutLogRepositoryImpl @Inject constructor(
    private val dao: SerieCompletadaDao,
) : WorkoutLogRepository {

    override fun observeTodayCompletedSets(): Flow<Map<Long, Set<Int>>> =
        dao.obtenerDelDia(todayKey()).map { series ->
            series.groupBy({ it.ejercicioId }, { it.numeroSerie })
                .mapValues { (_, numbers) -> numbers.toSet() }
        }

    override suspend fun setCompleted(exerciseId: Long, setNumber: Int, reps: Int, completed: Boolean) {
        val today = todayKey()
        if (completed) {
            dao.insertar(
                SerieCompletada(
                    ejercicioId = exerciseId,
                    fecha = today,
                    numeroSerie = setNumber,
                    repeticiones = reps,
                    completadaEn = System.currentTimeMillis(),
                )
            )
        } else {
            dao.eliminar(exerciseId, today, setNumber)
        }
    }

    /** Mismo formato que usa el contador de pasos ("yyyy-MM-dd"). */
    private fun todayKey(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}
