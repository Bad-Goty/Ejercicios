package com.goty.ejercicio.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.goty.ejercicio.R
import com.goty.ejercicio.ui.theme.BrandBlue
import com.goty.ejercicio.ui.theme.BrandOrange
import com.goty.ejercicio.ui.theme.EjercicioTheme
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

private val FigureSize = 36.dp
private val BadgeSize = 40.dp
private val BadgeIconSize = 22.dp

/** Cuánto se enciman los círculos entre sí (valor negativo = se superponen). */
private val BadgeOverlap = (-10).dp

/** Borde del color de la tarjeta para separar visualmente los círculos encimados. */
private val BadgeBorderWidth = 2.dp

private const val PROGRESS_ANIMATION_MS = 600

/**
 * Encabezado de Inicio: día, íconos de estadísticas y pasos de hoy hacia la meta.
 *
 * @param onGoalClick se llama al tocar la etiqueta de la meta (para cambiarla).
 * @param notice mensaje opcional debajo del progreso (p. ej. falta de permiso).
 */
@Composable
fun HomeHeader(
    dayName: String,
    steps: Int,
    goal: Int,
    onGoalClick: () -> Unit,
    modifier: Modifier = Modifier,
    notice: (@Composable () -> Unit)? = null,
) {
    val fraction = if (goal > 0) (steps.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val goalReached = goal > 0 && steps >= goal
    val numberFormat = remember { NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-MX")) }

    // La barra y la persona avanzan suavemente cuando cambian los pasos o la meta.
    // El valor se lee solo al dibujar/posicionar, sin recomponer en cada cuadro.
    val animatedFraction = animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(PROGRESS_ANIMATION_MS, easing = FastOutSlowInEasing),
        label = "stepsProgress",
    )
    // El número de pasos "cuenta" hasta el valor nuevo
    val animatedSteps by animateIntAsState(
        targetValue = steps,
        animationSpec = tween(PROGRESS_ANIMATION_MS, easing = FastOutSlowInEasing),
        label = "stepsCount",
    )

    // Círculo de la persona: gris mientras no se cumple la meta, azul al cumplirla
    val walkBadgeBackground by animateColorAsState(
        targetValue = if (goalReached) BrandBlue else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(PROGRESS_ANIMATION_MS),
        label = "walkBadgeBackground",
    )
    val walkBadgeIcon by animateColorAsState(
        targetValue = if (goalReached) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(PROGRESS_ANIMATION_MS),
        label = "walkBadgeIcon",
    )

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        // ---------- Día + íconos ----------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = dayName,
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(BadgeOverlap)) {
                IconBadge(
                    icon = R.drawable.ic_walking_person,
                    contentDescription = if (goalReached) "Meta de pasos cumplida" else "Meta de pasos pendiente",
                    background = walkBadgeBackground,
                    iconTint = walkBadgeIcon,
                    modifier = Modifier.zIndex(2f),
                )
                IconBadge(
                    icon = R.drawable.ic_flame,
                    contentDescription = "Calorías",
                    background = BrandOrange,
                    iconTint = Color.White,
                    modifier = Modifier.zIndex(1f),
                )
            }
        }

        HorizontalDivider()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            // ---------- Persona sobre el final de la parte llena de la barra ----------
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val travel = maxWidth - FigureSize
                Icon(
                    painter = painterResource(R.drawable.ic_walking_person),
                    contentDescription = null,
                    tint = BrandOrange,
                    modifier = Modifier
                        .offset {
                            IntOffset((travel.toPx() * animatedFraction.value).roundToInt(), 0)
                        }
                        .size(FigureSize),
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { animatedFraction.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = BrandOrange,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {},
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ---------- Pasos de hoy / meta (a la derecha) ----------
            Text(
                text = "${numberFormat.format(animatedSteps)}/${numberFormat.format(goal)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End),
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ---------- Meta (editable) + aviso de meta cumplida ----------
            Row(verticalAlignment = Alignment.CenterVertically) {
                GoalChip(
                    goalText = numberFormat.format(goal),
                    onClick = onGoalClick,
                )
                Spacer(modifier = Modifier.weight(1f))
                AnimatedVisibility(
                    visible = goalReached,
                    enter = fadeIn() + scaleIn(initialScale = 0.8f),
                    exit = fadeOut() + scaleOut(targetScale = 0.8f),
                ) {
                    Text(
                        text = "¡Meta cumplida!",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandBlue,
                    )
                }
            }

            if (notice != null) {
                Spacer(modifier = Modifier.height(12.dp))
                notice()
            }
        }
    }
}

/** Círculo de color con un ícono al centro. */
@Composable
private fun IconBadge(
    @DrawableRes icon: Int,
    contentDescription: String,
    background: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(BadgeSize)
            .border(BadgeBorderWidth, MaterialTheme.colorScheme.surface, CircleShape)
            .padding(BadgeBorderWidth)
            .background(color = background, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(BadgeIconSize),
        )
    }
}

/** Etiqueta con borde que muestra la meta; el lápiz indica que se puede tocar para cambiarla. */
@Composable
private fun GoalChip(goalText: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 10.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "META: $goalText",
                style = MaterialTheme.typography.labelLarge,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = "Cambiar meta",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

@Preview(showBackground = true, name = "En progreso (claro)")
@Composable
private fun HomeHeaderPreview() {
    EjercicioTheme(darkTheme = false) {
        HomeHeader(dayName = "Viernes", steps = 350, goal = 1000, onGoalClick = {},
            modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1B1B1F, name = "Meta cumplida (oscuro)")
@Composable
private fun HomeHeaderGoalReachedPreview() {
    EjercicioTheme(darkTheme = true) {
        HomeHeader(dayName = "Viernes", steps = 1200, goal = 1000, onGoalClick = {},
            modifier = Modifier.padding(16.dp))
    }
}
