package com.goty.ejercicio.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Tabla intermedia muchos a muchos: un ejercicio trabaja varios músculos
 * y un músculo aparece en varios ejercicios.
 * CASCADE: si se borra el ejercicio o el músculo, se borra la relación.
 */
@Entity(
    tableName = "ejercicio_musculo",
    primaryKeys = ["ejercicioId", "musculoId"],
    foreignKeys = [
        ForeignKey(
            entity = Ejercicio::class,
            parentColumns = ["id"],
            childColumns = ["ejercicioId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Musculo::class,
            parentColumns = ["id"],
            childColumns = ["musculoId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    // ejercicioId ya queda indexado por ser el primer campo de la PK compuesta.
    indices = [Index("musculoId")],
)
data class EjercicioMusculo(
    val ejercicioId: Long,
    val musculoId: Long,
    /** true = músculo principal, false = secundario. */
    @ColumnInfo(defaultValue = "0")
    val esPrincipal: Boolean = false,
)
