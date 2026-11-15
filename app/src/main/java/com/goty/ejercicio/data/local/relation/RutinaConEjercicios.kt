package com.goty.ejercicio.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.goty.ejercicio.data.local.entity.Ejercicio
import com.goty.ejercicio.data.local.entity.Rutina
import com.goty.ejercicio.data.local.entity.RutinaEjercicio

/** Una fila de rutina_ejercicio (series, reps...) junto con el ejercicio al que apunta. */
data class RutinaEjercicioConEjercicio(
    @Embedded val rutinaEjercicio: RutinaEjercicio,
    @Relation(
        parentColumn = "ejercicioId",
        entityColumn = "id",
    )
    val ejercicio: Ejercicio,
)

/** Una rutina con sus ejercicios y la configuración de cada uno. */
data class RutinaConEjercicios(
    @Embedded val rutina: Rutina,
    @Relation(
        entity = RutinaEjercicio::class,
        parentColumn = "id",
        entityColumn = "rutinaId",
    )
    val ejercicios: List<RutinaEjercicioConEjercicio>,
)
