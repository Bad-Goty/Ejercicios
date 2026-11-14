package com.goty.ejercicio.ui.navigation

import androidx.annotation.DrawableRes
import androidx.navigation3.runtime.NavKey
import com.goty.ejercicio.R

/**
 * Pestañas de la BottomBar. El orden aquí es el orden en pantalla.
 *
 * @param title texto de la TopBar.
 * @param label texto corto debajo del ícono en la BottomBar.
 */
enum class TopLevelDestination(
    val route: NavKey,
    val title: String,
    val label: String,
    @param:DrawableRes val icon: Int,
) {
    HOME(HomeRoute, title = "Inicio", label = "Inicio", R.drawable.ic_nav_home),
    ROUTINES(RoutinesRoute, title = "Rutinas", label = "Rutinas", R.drawable.ic_nav_routines),
    HISTORY(HistoryRoute, title = "Historial", label = "Historial", R.drawable.ic_nav_history),
    SETTINGS(SettingsRoute, title = "Configuraciones", label = "Ajustes", R.drawable.ic_nav_settings),
}
