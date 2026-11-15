package com.goty.ejercicio.data.mapper

import com.goty.ejercicio.data.local.entity.Ejercicio
import com.goty.ejercicio.domain.model.Exercise

fun Ejercicio.toDomain(): Exercise = Exercise(id = id, name = nombre)

fun Exercise.toEntity(): Ejercicio = Ejercicio(id = id, nombre = name)
