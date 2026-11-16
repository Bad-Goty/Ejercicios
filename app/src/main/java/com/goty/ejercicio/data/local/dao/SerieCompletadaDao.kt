package com.goty.ejercicio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goty.ejercicio.data.local.entity.SerieCompletada
import kotlinx.coroutines.flow.Flow

@Dao
interface SerieCompletadaDao {

    /** Registra una serie completada. Si ya estaba registrada, no hace nada. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(serie: SerieCompletada)

    /** Quita el registro (cuando se desmarca una serie). */
    @Query(
        "DELETE FROM serie_completada " +
            "WHERE ejercicioId = :ejercicioId AND fecha = :fecha AND numeroSerie = :numeroSerie"
    )
    suspend fun eliminar(ejercicioId: Long, fecha: String, numeroSerie: Int)

    /** Series completadas en un día ("yyyy-MM-dd"). */
    @Query("SELECT * FROM serie_completada WHERE fecha = :fecha")
    fun obtenerDelDia(fecha: String): Flow<List<SerieCompletada>>
}
