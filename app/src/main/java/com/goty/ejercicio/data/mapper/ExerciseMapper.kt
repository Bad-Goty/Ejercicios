package com.goty.ejercicio.data.mapper

import com.goty.ejercicio.data.local.entity.Ejercicio
import com.goty.ejercicio.data.local.relation.EjercicioConMusculoPrincipal
import com.goty.ejercicio.domain.model.Exercise

fun Ejercicio.toDomain(): Exercise = Exercise(
    id = id,
    name = nombre,
    series = series,
    reps = repeticiones,
    equipment = equipo,
    photo = foto,
)

fun EjercicioConMusculoPrincipal.toDomain(): Exercise =
    ejercicio.toDomain().copy(mainMuscleName = musculoNombre, mainMusclePhoto = musculoFoto)

fun Exercise.toEntity(): Ejercicio = Ejercicio(
    id = id,
    nombre = name,
    series = series,
    repeticiones = reps,
    equipo = equipment,
    foto = photo,
)
