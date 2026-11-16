package com.goty.ejercicio.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Qué categoría toca en qué día de una rutina. Un día puede tener varias categorías
 * (una fila por cada una), ej. Lunes -> Pecho y Lunes -> Hombros.
 * CASCADE: si se borra la rutina o la categoría, se borran estas filas.
 */
@Entity(
    tableName = "rutina_dia",
    primaryKeys = ["rutinaId", "diaSemana", "categoriaId"],
    foreignKeys = [
        ForeignKey(
            entity = Rutina::class,
            parentColumns = ["id"],
            childColumns = ["rutinaId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    // rutinaId ya queda indexado por ser el primer campo de la PK compuesta.
    indices = [Index("categoriaId")],
)
data class RutinaDia(
    val rutinaId: Long,
    /** Día de la semana: 1 = lunes ... 7 = domingo (igual que java.time.DayOfWeek.value). */
    val diaSemana: Int,
    val categoriaId: Long,
)
