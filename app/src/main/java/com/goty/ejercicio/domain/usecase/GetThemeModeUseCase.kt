package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.model.ThemeMode
import com.goty.ejercicio.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeModeUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    operator fun invoke(): Flow<ThemeMode> = repository.themeMode
}
