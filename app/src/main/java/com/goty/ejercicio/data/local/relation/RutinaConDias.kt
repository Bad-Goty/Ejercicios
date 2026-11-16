package com.goty.ejercicio.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.goty.ejercicio.data.local.entity.Categoria
import com.goty.ejercicio.data.local.entity.Rutina
import com.goty.ejercicio.data.local.entity.RutinaDia

/** Un día de la rutina junto con la categoría que toca ese día. */
data class RutinaDiaConCategoria(
    @Embedded val rutinaDia: RutinaDia,
    @Relation(
        parentColumn = "categoriaId",
        entityColumn = "id",
    )
    val categoria: Categoria,
)

/** Una rutina con sus días y la categoría (o categorías) de cada día. */
data class RutinaConDias(
    @Embedded val rutina: Rutina,
    @Relation(
        entity = RutinaDia::class,
        parentColumn = "id",
        entityColumn = "rutinaId",
    )
    val dias: List<RutinaDiaConCategoria>,
)
