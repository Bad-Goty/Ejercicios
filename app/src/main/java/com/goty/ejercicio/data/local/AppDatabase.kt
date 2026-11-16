package com.goty.ejercicio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.goty.ejercicio.data.local.converter.Converters
import com.goty.ejercicio.data.local.dao.CategoriaDao
import com.goty.ejercicio.data.local.dao.EjercicioDao
import com.goty.ejercicio.data.local.dao.MusculoDao
import com.goty.ejercicio.data.local.dao.RutinaDao
import com.goty.ejercicio.data.local.dao.SerieCompletadaDao
import com.goty.ejercicio.data.local.entity.Categoria
import com.goty.ejercicio.data.local.entity.Ejercicio
import com.goty.ejercicio.data.local.entity.EjercicioMusculo
import com.goty.ejercicio.data.local.entity.Musculo
import com.goty.ejercicio.data.local.entity.Rutina
import com.goty.ejercicio.data.local.entity.RutinaDia
import com.goty.ejercicio.data.local.entity.SerieCompletada

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
        RutinaDia::class,
        SerieCompletada::class,
    ],
    // v2: series/repeticiones en ejercicios y rutina_dia en lugar de rutina_ejercicio
    // v3: serie_completada (registro diario de series hechas). Ver Migrations.kt
    version = 3,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoriaDao(): CategoriaDao
    abstract fun musculoDao(): MusculoDao
    abstract fun ejercicioDao(): EjercicioDao
    abstract fun rutinaDao(): RutinaDao
    abstract fun serieCompletadaDao(): SerieCompletadaDao

    companion object {
        const val NAME = "ejercicio.db"
    }
}
