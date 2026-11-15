package com.goty.ejercicio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.goty.ejercicio.data.local.entity.Rutina
import com.goty.ejercicio.data.local.entity.RutinaEjercicio
import com.goty.ejercicio.data.local.relation.RutinaConEjercicios
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface RutinaDao {

    // --- CRUD de rutinas ---

    @Insert
    suspend fun insertar(rutina: Rutina): Long

    @Update
    suspend fun actualizar(rutina: Rutina)

    /** Borra también sus filas de rutina_ejercicio (CASCADE). */
    @Delete
    suspend fun eliminar(rutina: Rutina)

    @Query("SELECT * FROM rutinas ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<Rutina>>

    // --- Ejercicios dentro de una rutina ---

    @Insert
    suspend fun agregarEjercicio(rutinaEjercicio: RutinaEjercicio): Long

    /** Para cambiar series, reps, descanso u orden. */
    @Update
    suspend fun actualizarEjercicio(rutinaEjercicio: RutinaEjercicio)

    @Delete
    suspend fun quitarEjercicio(rutinaEjercicio: RutinaEjercicio)

    // --- Rutina con sus ejercicios ---

    @Transaction
    @Query("SELECT * FROM rutinas WHERE id = :id")
    fun obtenerConEjerciciosSinOrden(id: Long): Flow<RutinaConEjercicios?>

    /**
     * Rutina con sus ejercicios ordenados por "orden".
     * @Relation no permite ORDER BY, por eso se ordena aquí.
     */
    fun obtenerConEjercicios(id: Long): Flow<RutinaConEjercicios?> =
        obtenerConEjerciciosSinOrden(id).map { rutina ->
            rutina?.copy(ejercicios = rutina.ejercicios.sortedBy { it.rutinaEjercicio.orden })
        }
}
