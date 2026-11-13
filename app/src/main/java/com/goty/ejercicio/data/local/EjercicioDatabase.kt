package com.goty.ejercicio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.goty.ejercicio.data.local.dao.ExerciseDao
import com.goty.ejercicio.data.local.entity.ExerciseEntity

@Database(
    entities = [ExerciseEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class EjercicioDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        const val NAME = "ejercicio.db"
    }
}
