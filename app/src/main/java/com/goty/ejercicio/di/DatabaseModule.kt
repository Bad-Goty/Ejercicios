package com.goty.ejercicio.di

import android.content.Context
import androidx.room.Room
import com.goty.ejercicio.data.local.EjercicioDatabase
import com.goty.ejercicio.data.local.dao.ExerciseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EjercicioDatabase =
        Room.databaseBuilder(context, EjercicioDatabase::class.java, EjercicioDatabase.NAME)
            .build()

    @Provides
    fun provideExerciseDao(database: EjercicioDatabase): ExerciseDao = database.exerciseDao()
}
