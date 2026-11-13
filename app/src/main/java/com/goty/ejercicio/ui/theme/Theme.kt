package com.goty.ejercicio.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/** Duración de la transición de colores al cambiar entre claro y oscuro. */
private const val THEME_ANIMATION_MS = 600

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun EjercicioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme.animated(),
        typography = Typography,
        content = content
    )
}

/** Anima cada color de la paleta para que el cambio de tema sea una transición suave. */
@Composable
private fun ColorScheme.animated(): ColorScheme = copy(
    primary = primary.animate(),
    onPrimary = onPrimary.animate(),
    primaryContainer = primaryContainer.animate(),
    onPrimaryContainer = onPrimaryContainer.animate(),
    inversePrimary = inversePrimary.animate(),
    secondary = secondary.animate(),
    onSecondary = onSecondary.animate(),
    secondaryContainer = secondaryContainer.animate(),
    onSecondaryContainer = onSecondaryContainer.animate(),
    tertiary = tertiary.animate(),
    onTertiary = onTertiary.animate(),
    tertiaryContainer = tertiaryContainer.animate(),
    onTertiaryContainer = onTertiaryContainer.animate(),
    background = background.animate(),
    onBackground = onBackground.animate(),
    surface = surface.animate(),
    onSurface = onSurface.animate(),
    surfaceVariant = surfaceVariant.animate(),
    onSurfaceVariant = onSurfaceVariant.animate(),
    surfaceTint = surfaceTint.animate(),
    inverseSurface = inverseSurface.animate(),
    inverseOnSurface = inverseOnSurface.animate(),
    error = error.animate(),
    onError = onError.animate(),
    errorContainer = errorContainer.animate(),
    onErrorContainer = onErrorContainer.animate(),
    outline = outline.animate(),
    outlineVariant = outlineVariant.animate(),
    scrim = scrim.animate(),
    surfaceBright = surfaceBright.animate(),
    surfaceDim = surfaceDim.animate(),
    surfaceContainer = surfaceContainer.animate(),
    surfaceContainerHigh = surfaceContainerHigh.animate(),
    surfaceContainerHighest = surfaceContainerHighest.animate(),
    surfaceContainerLow = surfaceContainerLow.animate(),
    surfaceContainerLowest = surfaceContainerLowest.animate(),
)

@Composable
private fun Color.animate(): Color = animateColorAsState(
    targetValue = this,
    animationSpec = tween(durationMillis = THEME_ANIMATION_MS, easing = FastOutSlowInEasing),
    label = "themeColor",
).value