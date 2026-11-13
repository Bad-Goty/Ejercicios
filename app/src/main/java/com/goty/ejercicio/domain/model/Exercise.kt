package com.goty.ejercicio.domain.model

/**
 * Modelo de dominio: no depende de Room ni de Android.
 * Se irá ampliando conforme se definan los requisitos.
 */
data class Exercise(
    val id: Long = 0,
    val name: String,
)
