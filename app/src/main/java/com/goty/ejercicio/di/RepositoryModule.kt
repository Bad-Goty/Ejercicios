package com.goty.ejercicio.di

import com.goty.ejercicio.data.repository.ExerciseImageRepositoryImpl
import com.goty.ejercicio.data.repository.ExerciseRepositoryImpl
import com.goty.ejercicio.data.repository.MuscleRepositoryImpl
import com.goty.ejercicio.data.repository.RestTimerRepositoryImpl
import com.goty.ejercicio.data.repository.StepsRepositoryImpl
import com.goty.ejercicio.data.repository.UserPreferencesRepositoryImpl
import com.goty.ejercicio.data.repository.WorkoutLogRepositoryImpl
import com.goty.ejercicio.domain.repository.ExerciseImageRepository
import com.goty.ejercicio.domain.repository.ExerciseRepository
import com.goty.ejercicio.domain.repository.MuscleRepository
import com.goty.ejercicio.domain.repository.RestTimerRepository
import com.goty.ejercicio.domain.repository.StepsRepository
import com.goty.ejercicio.domain.repository.UserPreferencesRepository
import com.goty.ejercicio.domain.repository.WorkoutLogRepository
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
    abstract fun bindExerciseImageRepository(
        impl: ExerciseImageRepositoryImpl,
    ): ExerciseImageRepository

    @Binds
    @Singleton
    abstract fun bindMuscleRepository(impl: MuscleRepositoryImpl): MuscleRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl,
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindStepsRepository(impl: StepsRepositoryImpl): StepsRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutLogRepository(impl: WorkoutLogRepositoryImpl): WorkoutLogRepository

    @Binds
    @Singleton
    abstract fun bindRestTimerRepository(impl: RestTimerRepositoryImpl): RestTimerRepository
}
