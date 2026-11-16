package com.goty.ejercicio.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Registro de una serie completada en un día. Sirve para:
 * - Recordar qué series ya se hicieron hoy (aunque se cierre la app).
 * - Historial y estadísticas (por día, por semana...).
 *
 * Una fila por (ejercicio, día, número de serie). CASCADE: si se borra el ejercicio,
 * se borra su registro.
 */
@Entity(
    tableName = "serie_completada",
    primaryKeys = ["ejercicioId", "fecha", "numeroSerie"],
    foreignKeys = [
        ForeignKey(
            entity = Ejercicio::class,
            parentColumns = ["id"],
            childColumns = ["ejercicioId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    // ejercicioId ya queda indexado por ser el primer campo de la PK.
    // fecha se indexa para consultar rápido por día o por rango (semana, mes).
    indices = [Index("fecha")],
)
data class SerieCompletada(
    val ejercicioId: Long,
    /** Día en formato "yyyy-MM-dd" (se puede ordenar y comparar como texto). */
    val fecha: String,
    /** 1, 2, 3... */
    val numeroSerie: Int,
    /** Repeticiones hechas en esa serie. */
    val repeticiones: Int,
    /** Momento exacto en que se completó (epoch en milisegundos). */
    val completadaEn: Long,
)
