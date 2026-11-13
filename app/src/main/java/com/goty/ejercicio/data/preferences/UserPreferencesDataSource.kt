package com.goty.ejercicio.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/** Acceso a DataStore para preferencias del usuario. */
@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    /** Nombre del tema guardado, o null si el usuario nunca lo ha cambiado. */
    val themeMode: Flow<String?> = dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { preferences -> preferences[THEME_MODE] }

    suspend fun setThemeMode(value: String) {
        dataStore.edit { preferences -> preferences[THEME_MODE] = value }
    }

    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }
}
