package com.goty.ejercicio.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Destinos de la app. Deben ser @Serializable para que el back stack sobreviva a cambios de configuración. */
@Serializable
data object HomeRoute : NavKey
