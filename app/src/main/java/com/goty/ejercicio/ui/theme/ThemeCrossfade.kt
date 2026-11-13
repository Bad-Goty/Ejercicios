package com.goty.ejercicio.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.unit.IntSize

private const val CROSSFADE_MS = 450

/**
 * Cambia entre tema claro y oscuro con un fundido fluido.
 *
 * En lugar de animar los colores (lo que recompone toda la app en cada cuadro),
 * toma una captura de la pantalla con el tema anterior, aplica el tema nuevo
 * una sola vez y desvanece la captura encima. El fundido solo cambia la
 * transparencia de una imagen, así que no provoca recomposiciones.
 */
@Composable
fun ThemeCrossfade(
    darkTheme: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (darkTheme: Boolean) -> Unit,
) {
    val graphicsLayer = rememberGraphicsLayer()
    var appliedDarkTheme by remember { mutableStateOf(darkTheme) }
    var snapshot by remember { mutableStateOf<ImageBitmap?>(null) }
    val snapshotAlpha = remember { Animatable(0f) }

    LaunchedEffect(darkTheme) {
        if (darkTheme == appliedDarkTheme) return@LaunchedEffect

        // Si aún no se ha dibujado nada (p. ej. durante el splash), cambia sin animación
        val captured = if (graphicsLayer.size != IntSize.Zero) {
            runCatching { graphicsLayer.toImageBitmap() }.getOrNull()
        } else {
            null
        }

        if (captured == null) {
            appliedDarkTheme = darkTheme
            return@LaunchedEffect
        }

        snapshot = captured
        snapshotAlpha.snapTo(1f)
        appliedDarkTheme = darkTheme
        snapshotAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = CROSSFADE_MS, easing = FastOutSlowInEasing),
        )
        snapshot = null
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Contenido real, grabado en una capa para poder capturarlo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    graphicsLayer.record { this@drawWithContent.drawContent() }
                    drawLayer(graphicsLayer)
                },
        ) {
            content(appliedDarkTheme)
        }

        // Captura del tema anterior desvaneciéndose (el alpha se lee solo al dibujar)
        snapshot?.let { bitmap ->
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawImage(image = bitmap, alpha = snapshotAlpha.value)
            }
        }
    }
}
