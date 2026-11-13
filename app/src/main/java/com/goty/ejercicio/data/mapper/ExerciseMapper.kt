package com.goty.ejercicio.data.mapper

import com.goty.ejercicio.data.local.entity.ExerciseEntity
import com.goty.ejercicio.domain.model.Exercise

fun ExerciseEntity.toDomain(): Exercise = Exercise(id = id, name = name)

fun Exercise.toEntity(): ExerciseEntity = ExerciseEntity(id = id, name = name)
