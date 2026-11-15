package com.goty.ejercicio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.goty.ejercicio.data.local.converter.Converters
import com.goty.ejercicio.data.local.dao.CategoriaDao
import com.goty.ejercicio.data.local.dao.EjercicioDao
import com.goty.ejercicio.data.local.dao.MusculoDao
import com.goty.ejercicio.data.local.dao.RutinaDao
import com.goty.ejercicio.data.local.entity.Categoria
import com.goty.ejercicio.data.local.entity.Ejercicio
import com.goty.ejercicio.data.local.entity.EjercicioMusculo
import com.goty.ejercicio.data.local.entity.Musculo
import com.goty.ejercicio.data.local.entity.Rutina
import com.goty.ejercicio.data.local.entity.RutinaEjercicio

/**
 * Base de datos de la app. La instancia única la crea Hilt en di/DatabaseModule,
 * por eso no hay getInstance() aquí.
 */
@Database(
    entities = [
        Categoria::class,
        Musculo::class,
        Ejercicio::class,
        EjercicioMusculo::class,
        Rutina::class,
        RutinaEjercicio::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    abstract fun musculoDao(): MusculoDao
    abstract fun ejercicioDao(): EjercicioDao
    abstract fun rutinaDao(): RutinaDao

    companion object {
        const val NAME = "ejercicio.db"
    }
}
