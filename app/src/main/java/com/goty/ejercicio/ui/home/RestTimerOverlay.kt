package com.goty.ejercicio.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goty.ejercicio.ui.theme.BrandOrange

/**
 * Capa que cubre Inicio durante el descanso entre series.
 * Consume todos los toques y deslizamientos, así no se puede usar la pantalla de abajo
 * hasta que termine el temporizador.
 */
@Composable
fun RestTimerOverlay(
    state: RestTimerState,
    modifier: Modifier = Modifier,
) {
    val progress by animateFloatAsState(
        targetValue = state.secondsLeft.toFloat() / state.totalSeconds,
        animationSpec = tween(durationMillis = 1_000, easing = LinearEasing),
        label = "restProgress",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            // Bloquea la pantalla: se queda con todos los eventos de toque
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent().changes.forEach { it.consume() }
                    }
                }
            }
            .semantics { contentDescription = "Descanso: ${state.secondsLeft} segundos" },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = "DESCANSO",
                color = BrandOrange,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                fontSize = 16.sp,
            )
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(220.dp),
                    color = BrandOrange,
                    trackColor = Color.White.copy(alpha = 0.12f),
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round,
                )
                Text(
                    text = formatSeconds(state.secondsLeft),
                    color = Color.White,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = "Prepárate para la siguiente serie",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** 75 -> "1:15". */
private fun formatSeconds(totalSeconds: Int): String =
    "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)

@Preview
@Composable
private fun RestTimerOverlayPreview() {
    RestTimerOverlay(RestTimerState(secondsLeft = 42, totalSeconds = 60))
}
