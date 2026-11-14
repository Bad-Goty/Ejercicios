package com.goty.ejercicio.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator

/**
 * Crea el estado de navegación: una pila (back stack) por cada pestaña.
 * Sobrevive a cambios de configuración y a la muerte del proceso.
 */
@Composable
fun rememberNavigationState(
    startDestination: TopLevelDestination = TopLevelDestination.HOME,
): NavigationState {
    val currentTopLevel = rememberSaveable { mutableStateOf(startDestination) }
    val backStacks = TopLevelDestination.entries.associateWith { destination ->
        rememberNavBackStack(destination.route)
    }
    return remember(startDestination) {
        NavigationState(startDestination, currentTopLevel, backStacks)
    }
}

/**
 * Estado de navegación. No se modifica a sí mismo: lo modifica [Navigator].
 *
 * Sigue el patrón "salir por Inicio": la pila de Inicio siempre está debajo,
 * así que al regresar desde otra pestaña se vuelve a Inicio antes de salir.
 */
class NavigationState(
    val startDestination: TopLevelDestination,
    currentTopLevel: MutableState<TopLevelDestination>,
    val backStacks: Map<TopLevelDestination, NavBackStack<NavKey>>,
) {
    /** Pestaña seleccionada actualmente. */
    var currentTopLevel: TopLevelDestination by currentTopLevel

    /**
     * Lado por el que entra la siguiente pantalla ([SLIDE_FORWARD] o [SLIDE_BACKWARD]).
     * Lo fija [Navigator] justo antes de cada cambio y lo lee la animación de NavDisplay.
     */
    var slideDirection: Int = SLIDE_FORWARD

    /** Convierte las pilas en entradas para NavDisplay. Cada pestaña guarda su propio estado. */
    @Composable
    fun toDecoratedEntries(
        entryProvider: (NavKey) -> NavEntry<NavKey>,
    ): List<NavEntry<NavKey>> {
        val decoratedEntries = backStacks.mapValues { (_, stack) ->
            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = entryProvider,
            )
        }

        val stacksInUse = if (currentTopLevel == startDestination) {
            listOf(startDestination)
        } else {
            listOf(startDestination, currentTopLevel)
        }
        return stacksInUse.flatMap { decoratedEntries[it].orEmpty() }
    }
}
