package com.goty.ejercicio.domain.model

/** Categoría (ej. "Pecho") sin sus músculos. Se usa para planear qué toca cada día. */
data class Category(
    val id: Long,
    val name: String,
)
