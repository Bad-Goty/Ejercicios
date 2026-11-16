package com.goty.ejercicio.data.local.relation

import androidx.room.Embedded
import com.goty.ejercicio.data.local.entity.Ejercicio

/** Un ejercicio junto con el nombre y la imagen (campo foto) de su músculo principal, si tiene. */
data class EjercicioConMusculoPrincipal(
    @Embedded val ejercicio: Ejercicio,
    val musculoNombre: String?,
    val musculoFoto: String?,
)
