package com.goty.ejercicio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.goty.ejercicio.data.local.entity.Ejercicio
import com.goty.ejercicio.data.local.entity.EjercicioMusculo
import com.goty.ejercicio.data.local.relation.EjercicioConMusculos
import kotlinx.coroutines.flow.Flow

@Dao
interface EjercicioDao {

    // --- CRUD ---
    // Ojo: no se usa REPLACE porque internamente hace DELETE + INSERT y, con CASCADE,
    // borraría las relaciones del ejercicio. Para modificar se usa actualizar().

    @Insert
    suspend fun insertar(ejercicio: Ejercicio): Long

    @Update
    suspend fun actualizar(ejercicio: Ejercicio)

    @Delete
    suspend fun eliminar(ejercicio: Ejercicio)

    // --- Consultas ---

    @Query("SELECT * FROM ejercicios ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Ejercicio>>

    @Transaction
    @Query("SELECT * FROM ejercicios WHERE id = :id")
    fun obtenerConMusculos(id: Long): Flow<EjercicioConMusculos?>

    @Query(
        """
        SELECT e.* FROM ejercicios e
        INNER JOIN ejercicio_musculo em ON em.ejercicioId = e.id
        WHERE em.musculoId = :musculoId
        ORDER BY e.nombre ASC
        """
    )
    fun obtenerPorMusculo(musculoId: Long): Flow<List<Ejercicio>>

    @Query("SELECT * FROM ejercicios WHERE esFavorito = 1 ORDER BY nombre ASC")
    fun obtenerFavoritos(): Flow<List<Ejercicio>>

    @Query("SELECT * FROM ejercicios WHERE nombre LIKE '%' || :texto || '%' ORDER BY nombre ASC")
    fun buscarPorNombre(texto: String): Flow<List<Ejercicio>>

    // --- Relación ejercicio <-> músculo ---

    /** Si la relación ya existe, la reemplaza (útil para cambiar esPrincipal). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMusculos(relaciones: List<EjercicioMusculo>)

    @Delete
    suspend fun eliminarMusculo(relacion: EjercicioMusculo)

    /**
     * Inserta el ejercicio y, si hay, su músculo principal, todo en una transacción
     * (si algo falla no queda un ejercicio a medias). Devuelve el id del ejercicio.
     */
    @Transaction
    suspend fun insertarConMusculoPrincipal(ejercicio: Ejercicio, musculoPrincipalId: Long?): Long {
        val ejercicioId = insertar(ejercicio)
        if (musculoPrincipalId != null) {
            insertarMusculos(listOf(EjercicioMusculo(ejercicioId, musculoPrincipalId, esPrincipal = true)))
        }
        return ejercicioId
    }
}
