package com.goty.ejercicio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.goty.ejercicio.data.local.entity.Categoria
import com.goty.ejercicio.data.local.relation.CategoriaConMusculos
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    /** Devuelve el id generado. */
    @Insert
    suspend fun insertar(categoria: Categoria): Long

    @Update
    suspend fun actualizar(categoria: Categoria)

    /** Lanza SQLiteConstraintException si la categoría todavía tiene músculos (RESTRICT). */
    @Delete
    suspend fun eliminar(categoria: Categoria)

    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<Categoria>>

    /** Categorías (en el orden de la precarga) con sus músculos. */
    @Transaction
    @Query("SELECT * FROM categorias ORDER BY id ASC")
    fun obtenerConMusculos(): Flow<List<CategoriaConMusculos>>
}
