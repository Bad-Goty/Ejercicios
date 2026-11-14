package com.goty.ejercicio.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.goty.ejercicio.ui.components.AppBottomBar
import com.goty.ejercicio.ui.components.AppTopBar
import com.goty.ejercicio.ui.history.HistoryScreen
import com.goty.ejercicio.ui.home.HomeScreen
import com.goty.ejercicio.ui.routines.RoutinesScreen
import com.goty.ejercicio.ui.settings.SettingsScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navigationState = rememberNavigationState()
    val navigator = remember(navigationState) { Navigator(navigationState) }

    val entryProvider = entryProvider {
        entry<HomeRoute> { HomeScreen() }
        entry<RoutinesRoute> { RoutinesScreen() }
        entry<HistoryRoute> { HistoryScreen() }
        entry<SettingsRoute> { SettingsScreen() }
    }

    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = navigationState.currentTopLevel.title) },
        bottomBar = {
            AppBottomBar(
                selected = navigationState.currentTopLevel,
                onSelect = navigator::selectTopLevel,
            )
        },
    ) { innerPadding ->
        NavDisplay(
            entries = navigationState.toDecoratedEntries(entryProvider),
            onBack = { navigator.goBack() },
            modifier = Modifier.padding(innerPadding),
            // La dirección la decide Navigator según la posición de la pestaña
            transitionSpec = { tabSlideTransition(navigationState.slideDirection) },
            popTransitionSpec = { tabSlideTransition(navigationState.slideDirection) },
            // Gesto de atrás: siempre regresa, así que entra por la izquierda
            predictivePopTransitionSpec = { tabSlideTransition(SLIDE_BACKWARD) },
        )
    }
}
