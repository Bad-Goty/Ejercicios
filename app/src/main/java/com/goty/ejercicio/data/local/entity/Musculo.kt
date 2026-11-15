package com.goty.ejercicio.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Músculo que pertenece a una [Categoria].
 * RESTRICT: no se puede borrar una categoría mientras tenga músculos.
 */
@Entity(
    tableName = "musculos",
    foreignKeys = [
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("categoriaId")],
)
data class Musculo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoriaId: Long,
    val nombre: String,
    /** Agrupa músculos, ej. "Cuádriceps", "Tríceps". null si no pertenece a un grupo. */
    val grupo: String? = null,
    /** Ruta o URI de la foto. */
    val foto: String? = null,
)
