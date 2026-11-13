package com.goty.ejercicio.di

import com.goty.ejercicio.data.repository.ExerciseRepositoryImpl
import com.goty.ejercicio.data.repository.UserPreferencesRepositoryImpl
import com.goty.ejercicio.domain.repository.ExerciseRepository
import com.goty.ejercicio.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(impl: ExerciseRepositoryImpl): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl,
    ): UserPreferencesRepository
}
