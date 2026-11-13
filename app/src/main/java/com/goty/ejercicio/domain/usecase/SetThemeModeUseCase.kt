package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.model.ThemeMode
import com.goty.ejercicio.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(mode: ThemeMode) = repository.setThemeMode(mode)
}
