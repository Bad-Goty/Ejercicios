package com.goty.ejercicio.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.goty.ejercicio.data.local.entity.Categoria
import com.goty.ejercicio.data.local.entity.Rutina
import com.goty.ejercicio.data.local.entity.RutinaDia
import com.goty.ejercicio.data.local.relation.RutinaConDias
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface RutinaDao {

    // --- CRUD de rutinas ---

    @Insert
    suspend fun insertar(rutina: Rutina): Long

    @Update
    suspend fun actualizar(rutina: Rutina)

    /** Borra también sus días (CASCADE). */
    @Delete
    suspend fun eliminar(rutina: Rutina)

    @Query("SELECT * FROM rutinas ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<Rutina>>

    // --- Qué categoría toca cada día ---

    /** Asigna una categoría a un día. Si ya estaba asignada, no hace nada. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun asignarCategoria(rutinaDia: RutinaDia)

    @Delete
    suspend fun quitarCategoria(rutinaDia: RutinaDia)

    /** Categorías que tocan un día (1 = lunes ... 7 = domingo). */
    @Query(
        """
        SELECT c.* FROM categorias c
        INNER JOIN rutina_dia rd ON rd.categoriaId = c.id
        WHERE rd.rutinaId = :rutinaId AND rd.diaSemana = :diaSemana
        ORDER BY c.id ASC
        """
    )
    fun obtenerCategoriasDelDia(rutinaId: Long, diaSemana: Int): Flow<List<Categoria>>

    // --- Rutina con sus días ---

    @Transaction
    @Query("SELECT * FROM rutinas WHERE id = :id")
    fun obtenerConDiasSinOrden(id: Long): Flow<RutinaConDias?>

    /**
     * Rutina con sus días ordenados de lunes a domingo.
     * @Relation no permite ORDER BY, por eso se ordena aquí.
     */
    fun obtenerConDias(id: Long): Flow<RutinaConDias?> =
        obtenerConDiasSinOrden(id).map { rutina ->
            rutina?.copy(
                dias = rutina.dias.sortedWith(
                    compareBy({ it.rutinaDia.diaSemana }, { it.rutinaDia.categoriaId })
                )
            )
        }
}
