package com.goty.ejercicio.ui.navigation

import androidx.navigation3.runtime.NavKey

/** Maneja los eventos de navegación actualizando [NavigationState]. */
class Navigator(private val state: NavigationState) {

    /** Cambia de pestaña. La pestaña conserva las pantallas que tenía abiertas. */
    fun selectTopLevel(destination: TopLevelDestination) {
        if (destination == state.currentTopLevel) return

        // Si la pestaña está a la derecha, la pantalla entra por la derecha (y viceversa)
        state.slideDirection = if (destination.ordinal > state.currentTopLevel.ordinal) {
            SLIDE_FORWARD
        } else {
            SLIDE_BACKWARD
        }
        state.currentTopLevel = destination
    }

    /** Abre una pantalla dentro de la pestaña actual (p. ej. un detalle). */
    fun navigate(route: NavKey) {
        state.slideDirection = SLIDE_FORWARD
        state.backStacks.getValue(state.currentTopLevel).add(route)
    }

    fun goBack() {
        state.slideDirection = SLIDE_BACKWARD
        val stack = state.backStacks.getValue(state.currentTopLevel)
        if (stack.size > 1) {
            stack.removeLastOrNull()
        } else {
            // En la raíz de una pestaña: regresa a Inicio
            state.currentTopLevel = state.startDestination
        }
    }
}
