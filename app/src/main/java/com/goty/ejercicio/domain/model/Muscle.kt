package com.goty.ejercicio.domain.model

/** Modelo de dominio de un músculo. */
data class Muscle(
    val id: Long,
    val name: String,
    val group: String?,
    /** Nombre del drawable de la imagen, ej. "ic_musculo_biceps_braquial". */
    val photo: String?,
)

/** Categoría (ej. "Pecho") con sus músculos. */
data class MuscleCategory(
    val id: Long,
    val name: String,
    val icon: String?,
    val muscles: List<Muscle>,
)
