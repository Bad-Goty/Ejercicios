package com.goty.ejercicio.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.goty.ejercicio.data.local.entity.Categoria
import com.goty.ejercicio.data.local.entity.Musculo

/** Un músculo junto con su categoría. */
data class MusculoConCategoria(
    @Embedded val musculo: Musculo,
    @Relation(
        parentColumn = "categoriaId",
        entityColumn = "id",
    )
    val categoria: Categoria,
)
