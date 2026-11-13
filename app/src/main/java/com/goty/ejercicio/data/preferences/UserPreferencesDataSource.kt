package com.goty.ejercicio.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Acceso a DataStore para preferencias del usuario (ajustes, unidades, etc.).
 * Aquí se agregarán las claves cuando se definan.
 */
@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
)
