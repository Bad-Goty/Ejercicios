package com.goty.ejercicio.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.goty.ejercicio.data.local.entity.Ejercicio
import com.goty.ejercicio.data.local.entity.EjercicioMusculo
import com.goty.ejercicio.data.local.entity.Musculo

/** Un ejercicio con todos sus músculos, pasando por la tabla intermedia ejercicio_musculo. */
data class EjercicioConMusculos(
    @Embedded val ejercicio: Ejercicio,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = EjercicioMusculo::class,
            parentColumn = "ejercicioId",
            entityColumn = "musculoId",
        ),
    )
    val musculos: List<Musculo>,
)
