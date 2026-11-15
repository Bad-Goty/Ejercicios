package com.goty.ejercicio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Agrupa músculos (ej. "Tren superior"). */
@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    /** Ruta o URI del ícono. */
    val icono: String? = null,
)
