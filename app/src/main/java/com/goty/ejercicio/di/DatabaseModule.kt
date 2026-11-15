package com.goty.ejercicio.di

import android.content.Context
import androidx.room.Room
import com.goty.ejercicio.data.local.AppDatabase
import com.goty.ejercicio.data.local.PrepopulateCallback
import com.goty.ejercicio.data.local.dao.CategoriaDao
import com.goty.ejercicio.data.local.dao.EjercicioDao
import com.goty.ejercicio.data.local.dao.MusculoDao
import com.goty.ejercicio.data.local.dao.RutinaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /** @Singleton = una sola instancia en toda la app (reemplaza al getInstance clásico). */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME)
            .addCallback(PrepopulateCallback())
            .build()

    @Provides
    fun provideCategoriaDao(database: AppDatabase): CategoriaDao = database.categoriaDao()

    @Provides
    fun provideMusculoDao(database: AppDatabase): MusculoDao = database.musculoDao()

    @Provides
    fun provideEjercicioDao(database: AppDatabase): EjercicioDao = database.ejercicioDao()

    @Provides
    fun provideRutinaDao(database: AppDatabase): RutinaDao = database.rutinaDao()
}
