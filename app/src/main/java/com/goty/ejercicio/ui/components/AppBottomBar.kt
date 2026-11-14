package com.goty.ejercicio.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.goty.ejercicio.ui.navigation.TAB_ANIMATION_MS
import com.goty.ejercicio.ui.navigation.TopLevelDestination
import com.goty.ejercicio.ui.theme.BrandOrange
import com.goty.ejercicio.ui.theme.EjercicioTheme
import kotlin.math.roundToInt

// Medidas de la barra de navegación de Material 3
private val BarHeight = 80.dp
private val IndicatorTopPadding = 12.dp
private val IndicatorWidth = 64.dp
private val IndicatorHeight = 32.dp

/**
 * Barra inferior con las pestañas principales.
 *
 * Igual a la NavigationBar de Material 3, pero el indicador de la pestaña
 * seleccionada se desliza de una pestaña a otra, sincronizado con la
 * animación de las pantallas.
 */
@Composable
fun AppBottomBar(
    selected: TopLevelDestination,
    onSelect: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val destinations = TopLevelDestination.entries

    // Posición del indicador (0 = primera pestaña). Se lee solo en la fase de layout.
    val indicatorPosition = animateFloatAsState(
        targetValue = selected.ordinal.toFloat(),
        animationSpec = tween(durationMillis = TAB_ANIMATION_MS, easing = FastOutSlowInEasing),
        label = "bottomBarIndicator",
    )

    Surface(
        modifier = modifier,
        color = NavigationBarDefaults.containerColor,
        tonalElevation = NavigationBarDefaults.Elevation,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                .height(BarHeight),
        ) {
            val itemWidth = maxWidth / destinations.size

            // Indicador que se desliza detrás de los íconos
            Box(
                modifier = Modifier
                    .offset {
                        val x = itemWidth.toPx() * indicatorPosition.value +
                            (itemWidth - IndicatorWidth).toPx() / 2
                        IntOffset(x.roundToInt(), IndicatorTopPadding.roundToPx())
                    }
                    .size(IndicatorWidth, IndicatorHeight)
                    .background(BrandOrange.copy(alpha = 0.15f), CircleShape),
            )

            Row(modifier = Modifier.fillMaxSize()) {
                destinations.forEach { destination ->
                    BottomBarItem(
                        destination = destination,
                        selected = destination == selected,
                        onClick = { onSelect(destination) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    destination: TopLevelDestination,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val colorSpec = tween<Color>(TAB_ANIMATION_MS)
    val iconColor by animateColorAsState(
        targetValue = if (selected) BrandOrange else colors.onSurfaceVariant,
        animationSpec = colorSpec,
        label = "bottomBarIconColor",
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) BrandOrange else colors.onSurfaceVariant,
        animationSpec = colorSpec,
        label = "bottomBarLabelColor",
    )

    Column(
        modifier = modifier
            .selectable(
                selected = selected,
                // Sin sombreado (ripple) al tocar: el indicador deslizante ya marca la selección
                interactionSource = null,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .padding(top = IndicatorTopPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Box(
            modifier = Modifier.size(IndicatorWidth, IndicatorHeight),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(destination.icon),
                contentDescription = null,
                tint = iconColor,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = destination.label,
            style = MaterialTheme.typography.labelMedium,
            color = labelColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppBottomBarPreview() {
    EjercicioTheme {
        AppBottomBar(selected = TopLevelDestination.HOME, onSelect = {})
    }
}
