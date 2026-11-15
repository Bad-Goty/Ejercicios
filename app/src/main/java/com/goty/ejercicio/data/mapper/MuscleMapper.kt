package com.goty.ejercicio.data.mapper

import com.goty.ejercicio.data.local.entity.Musculo
import com.goty.ejercicio.data.local.relation.CategoriaConMusculos
import com.goty.ejercicio.domain.model.Muscle
import com.goty.ejercicio.domain.model.MuscleCategory

fun Musculo.toDomain(): Muscle = Muscle(id = id, name = nombre, group = grupo, photo = foto)

/** @Relation no garantiza orden, por eso los músculos se ordenan por id (orden de la precarga). */
fun CategoriaConMusculos.toDomain(): MuscleCategory = MuscleCategory(
    id = categoria.id,
    name = categoria.nombre,
    icon = categoria.icono,
    muscles = musculos.sortedBy { it.id }.map { it.toDomain() },
)
