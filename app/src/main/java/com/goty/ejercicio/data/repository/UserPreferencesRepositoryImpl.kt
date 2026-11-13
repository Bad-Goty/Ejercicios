package com.goty.ejercicio.data.repository

import com.goty.ejercicio.data.preferences.UserPreferencesDataSource
import com.goty.ejercicio.domain.model.ThemeMode
import com.goty.ejercicio.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataSource: UserPreferencesDataSource,
) : UserPreferencesRepository {

    override val themeMode: Flow<ThemeMode> = dataSource.themeMode.map { name ->
        ThemeMode.entries.firstOrNull { it.name == name } ?: ThemeMode.SYSTEM
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataSource.setThemeMode(mode.name)
    }
}
