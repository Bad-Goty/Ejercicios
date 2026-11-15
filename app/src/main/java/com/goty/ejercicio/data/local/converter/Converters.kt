package com.goty.ejercicio.data.local.converter

import androidx.room.TypeConverter
import com.goty.ejercicio.data.local.entity.TipoMedicion

/** Convierte tipos que SQLite no entiende a tipos que sí (aquí: enum <-> String). */
class Converters {

    @TypeConverter
    fun fromTipoMedicion(tipo: TipoMedicion): String = tipo.name

    @TypeConverter
    fun toTipoMedicion(valor: String): TipoMedicion = TipoMedicion.valueOf(valor)
}
