package com.goty.ejercicio.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Un ejercicio dentro de una rutina, con su configuración (series, reps/tiempo, descanso).
 * Tiene id propio para permitir el mismo ejercicio dos veces en una rutina.
 * CASCADE: al borrar la rutina o el ejercicio, se borran estas filas.
 */
@Entity(
    tableName = "rutina_ejercicio",
    foreignKeys = [
        ForeignKey(
            entity = Rutina::class,
            parentColumns = ["id"],
            childColumns = ["rutinaId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Ejercicio::class,
            parentColumns = ["id"],
            childColumns = ["ejercicioId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("rutinaId"), Index("ejercicioId")],
)
data class RutinaEjercicio(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rutinaId: Long,
    val ejercicioId: Long,
    /** Posición dentro de la rutina (0, 1, 2...). */
    val orden: Int,
    val series: Int,
    /** Se usa si el ejercicio es [TipoMedicion.REPETICIONES]. */
    val repeticiones: Int? = null,
    /** Se usa si el ejercicio es [TipoMedicion.TIEMPO]. */
    val duracionSegundos: Int? = null,
    val descansoSegundos: Int? = null,
)
