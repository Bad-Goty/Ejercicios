package com.goty.ejercicio.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.goty.ejercicio.data.local.entity.Categoria
import com.goty.ejercicio.data.local.entity.Musculo

/** Una categoría con todos sus músculos. */
data class CategoriaConMusculos(
    @Embedded val categoria: Categoria,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoriaId",
    )
    val musculos: List<Musculo>,
)
