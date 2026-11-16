package com.goty.ejercicio.domain.model

/**
 * Modelo de dominio: no depende de Room ni de Android.
 * Se irá ampliando conforme se definan los requisitos.
 */
data class Exercise(
    val id: Long = 0,
    val name: String,
    /** Número de series. */
    val series: Int = 4,
    /** Repeticiones por serie. */
    val reps: Int = 12,
    /** Equipo, ej. "Mancuernas / 30°" (null si no se indicó). */
    val equipment: String? = null,
    /** Ruta de la foto/GIF guía que agregó el usuario (null si no tiene). */
    val photo: String? = null,
    /** Nombre del músculo principal, ej. "Bíceps braquial" (null si no tiene). */
    val mainMuscleName: String? = null,
    /** Imagen del músculo principal, ej. "ic_musculo_biceps_braquial" (null si no tiene). */
    val mainMusclePhoto: String? = null,
)
