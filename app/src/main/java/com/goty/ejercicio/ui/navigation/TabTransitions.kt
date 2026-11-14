package com.goty.ejercicio.ui.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.unit.IntOffset

/** Duración compartida por el deslizamiento de pantallas y el indicador de la BottomBar. */
const val TAB_ANIMATION_MS = 350

/** La pantalla nueva entra por la derecha (pestaña a la derecha o pantalla nueva). */
const val SLIDE_FORWARD = 1

/** La pantalla nueva entra por la izquierda (pestaña a la izquierda o regresar). */
const val SLIDE_BACKWARD = -1

/**
 * Desliza horizontalmente: la pantalla nueva entra desde el lado indicado por
 * [direction] y la anterior sale por el lado contrario.
 */
fun tabSlideTransition(direction: Int): ContentTransform {
    val spec = tween<IntOffset>(durationMillis = TAB_ANIMATION_MS, easing = FastOutSlowInEasing)
    return slideInHorizontally(spec) { fullWidth -> fullWidth * direction } togetherWith
        slideOutHorizontally(spec) { fullWidth -> -fullWidth * direction }
}
