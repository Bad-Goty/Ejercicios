package com.goty.ejercicio.data.mapper

import com.goty.ejercicio.data.local.entity.Categoria
import com.goty.ejercicio.domain.model.Category

fun Categoria.toDomain(): Category = Category(id = id, name = nombre)
