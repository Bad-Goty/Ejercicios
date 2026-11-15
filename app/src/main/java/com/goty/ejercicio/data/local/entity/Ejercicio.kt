package com.goty.ejercicio.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Ejercicio del catálogo. Las series/repeticiones NO van aquí, van en [RutinaEjercicio],
 * porque el mismo ejercicio puede hacerse distinto en cada rutina.
 */
@Entity(tableName = "ejercicios")
data class Ejercicio(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    /** Instrucciones de cómo se hace. */
    val descripcion: String? = null,
    /** Barra, mancuerna, peso corporal, etc. */
    val equipo: String? = null,
    @ColumnInfo(defaultValue = "REPETICIONES")
    val tipoMedicion: TipoMedicion = TipoMedicion.REPETICIONES,
    /** Ruta o URI de la foto (la imagen NO se guarda en la BD). */
    val foto: String? = null,
    @ColumnInfo(defaultValue = "0")
    val esFavorito: Boolean = false,
)
