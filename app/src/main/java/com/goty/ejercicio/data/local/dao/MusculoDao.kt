package com.goty.ejercicio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.goty.ejercicio.data.local.entity.Musculo
import com.goty.ejercicio.data.local.relation.MusculoConCategoria
import kotlinx.coroutines.flow.Flow

@Dao
interface MusculoDao {

    @Insert
    suspend fun insertar(musculo: Musculo): Long

    @Update
    suspend fun actualizar(musculo: Musculo)

    @Delete
    suspend fun eliminar(musculo: Musculo)

    @Query("SELECT * FROM musculos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Musculo>>

    @Query("SELECT * FROM musculos WHERE categoriaId = :categoriaId ORDER BY nombre ASC")
    fun obtenerPorCategoria(categoriaId: Long): Flow<List<Musculo>>

    @Transaction
    @Query("SELECT * FROM musculos WHERE id = :id")
    fun obtenerConCategoria(id: Long): Flow<MusculoConCategoria?>

    @Transaction
    @Query("SELECT * FROM musculos ORDER BY nombre ASC")
    fun obtenerTodosConCategoria(): Flow<List<MusculoConCategoria>>
}
