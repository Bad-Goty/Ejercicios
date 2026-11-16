package com.goty.ejercicio.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.goty.ejercicio.R
import com.goty.ejercicio.ui.theme.BrandOrange
import com.goty.ejercicio.ui.theme.EjercicioTheme

/** Una serie del ejercicio: número, repeticiones y si ya se completó. */
data class ExerciseSetUi(
    val number: Int,
    val reps: Int,
    val done: Boolean = false,
)

/** Ancho de las columnas REPS y ESTADO, compartido por el encabezado y las filas. */
private val RepsColumnWidth = 96.dp
private val StatusColumnWidth = 56.dp

/**
 * Tarjeta de un ejercicio que se expande al tocar la cabecera.
 *
 * - Cabecera (siempre visible): miniatura, nombre, músculo principal, equipo y progreso de series.
 * - Expandida: tabla SERIE / REPS / ESTADO. La primera serie sin completar se resalta como
 *   "OBJETIVO"; al final, la foto/GIF del ejercicio si tiene.
 *
 * @param thumbnail drawable de la miniatura (ej. la imagen del músculo principal). Si es null
 *   se muestra el ícono de pesa.
 * @param muscleName nombre del músculo principal; se muestra en una etiqueta naranja.
 * @param equipment equipo, ej. "Mancuernas / 30°".
 * @param photo ruta o URI de la foto/GIF del ejercicio. Si es null no se muestra esa sección.
 * @param onSetCheckedChange se llama con el índice de la serie y el nuevo valor de la casilla.
 */
@Composable
fun ExerciseCard(
    name: String,
    sets: List<ExerciseSetUi>,
    onSetCheckedChange: (index: Int, done: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes thumbnail: Int? = null,
    muscleName: String? = null,
    equipment: String? = null,
    photo: String? = null,
    initiallyExpanded: Boolean = false,
) {
    var expanded by rememberSaveable { mutableStateOf(initiallyExpanded) }
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, label = "arrow")
    // La serie "objetivo" es la primera que falta por completar
    val targetIndex = sets.indexOfFirst { !it.done }
    // Las series se hacen en orden: solo se puede marcar la objetivo y desmarcar la última
    // completada (por si fue un error). Las demás quedan deshabilitadas.
    val lastDoneIndex = if (targetIndex == -1) sets.lastIndex else targetIndex - 1

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        // --- Cabecera ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            ExerciseThumbnail(thumbnail)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (muscleName != null || equipment != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (muscleName != null) {
                            MuscleChip(muscleName, Modifier.weight(1f, fill = false))
                        }
                        if (equipment != null) {
                            Text(
                                text = equipment,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                SetsProgress(sets)
            }
            Icon(
                painter = painterResource(R.drawable.ic_expand_more),
                contentDescription = if (expanded) "Contraer" else "Expandir",
                modifier = Modifier.rotate(arrowRotation),
            )
        }

        // --- Detalle ---
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut(),
        ) {
            Column {
                if (sets.isNotEmpty()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    SetsTableHeader()
                    Column(
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        sets.forEachIndexed { index, set ->
                            SetRow(
                                set = set,
                                isTarget = index == targetIndex,
                                enabled = index == targetIndex || index == lastDoneIndex,
                                onCheckedChange = { onSetCheckedChange(index, it) },
                            )
                        }
                    }
                }
                if (photo != null) {
                    AsyncImage(
                        model = photo,
                        contentDescription = "Demostración de $name",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(14.dp)),
                    )
                }
            }
        }
    }
}

/** Miniatura cuadrada con borde y esquinas redondeadas. */
@Composable
private fun ExerciseThumbnail(@DrawableRes thumbnail: Int?) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape),
        contentAlignment = Alignment.Center,
    ) {
        if (thumbnail != null) {
            Image(
                painter = painterResource(thumbnail),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.padding(6.dp),
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_fitness_center),
                contentDescription = null,
                tint = BrandOrange,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

/** Etiqueta naranja con el músculo principal. */
@Composable
private fun MuscleChip(text: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(6.dp)
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = BrandOrange,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .background(BrandOrange.copy(alpha = 0.12f), shape)
            .border(1.dp, BrandOrange.copy(alpha = 0.4f), shape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

/** Barritas de progreso (una por serie) y el texto "1 / 3 series completadas". */
@Composable
private fun SetsProgress(sets: List<ExerciseSetUi>) {
    if (sets.isEmpty()) {
        Text(
            text = "Sin series",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }
    val doneCount = sets.count { it.done }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            sets.forEach { set ->
                Box(
                    Modifier
                        .size(width = 16.dp, height = 6.dp)
                        .background(
                            if (set.done) BrandOrange else MaterialTheme.colorScheme.outlineVariant,
                            CircleShape,
                        )
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(
            text = "$doneCount / ${sets.size}",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (doneCount == sets.size) BrandOrange else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Encabezado de la tabla: SERIE / REPS / ESTADO. */
@Composable
private fun SetsTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 14.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TableHeaderText("SERIE", Modifier.weight(1f))
        TableHeaderText("REPS", Modifier.width(RepsColumnWidth), TextAlign.Center)
        TableHeaderText("ESTADO", Modifier.width(StatusColumnWidth), TextAlign.Center)
    }
}

@Composable
private fun TableHeaderText(text: String, modifier: Modifier, textAlign: TextAlign = TextAlign.Start) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = textAlign,
        modifier = modifier,
    )
}

/**
 * Fila de una serie.
 * - Completada: tachada y atenuada, casilla naranja con palomita.
 * - Objetivo (la siguiente por hacer): borde naranja, punto a la izquierda y "OBJETIVO".
 * - Pendiente: atenuada.
 * Si [enabled] es false no se puede tocar (series fuera de orden).
 */
@Composable
private fun SetRow(
    set: ExerciseSetUi,
    isTarget: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    val rowModifier = when {
        isTarget -> Modifier
            .background(BrandOrange.copy(alpha = 0.06f), shape)
            .border(1.5.dp, BrandOrange, shape)
        set.done -> Modifier
            .background(MaterialTheme.colorScheme.surfaceContainer, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
        else -> Modifier
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), shape)
    }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(rowModifier)
                .clip(shape)
                .toggleable(
                    value = set.done,
                    enabled = enabled,
                    role = Role.Checkbox,
                    onValueChange = onCheckedChange,
                )
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // SERIE
            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(if (set.done || isTarget) 1f else 0.6f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "Serie ${set.number}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (set.done) TextDecoration.LineThrough else null,
                    color = if (set.done) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface,
                )
                when {
                    set.done -> Text(
                        text = "Completada",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    isTarget -> Text(
                        text = "OBJETIVO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = BrandOrange,
                    )
                }
            }

            // REPS
            Box(Modifier.width(RepsColumnWidth), contentAlignment = Alignment.Center) {
                RepsValue(reps = set.reps, highlighted = isTarget, dimmed = !set.done && !isTarget)
            }

            // ESTADO
            Box(Modifier.width(StatusColumnWidth), contentAlignment = Alignment.Center) {
                StatusBox(done = set.done, enabled = enabled)
            }
        }

        // Punto naranja sobre el borde izquierdo de la serie objetivo
        if (isTarget) {
            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-5).dp)
                    .size(10.dp)
                    .background(BrandOrange, CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}

/** "12 reps": en la serie objetivo va dentro de un recuadro. */
@Composable
private fun RepsValue(reps: Int, highlighted: Boolean, dimmed: Boolean) {
    val content: @Composable () -> Unit = {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = reps.toString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "reps",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    if (highlighted) {
        val shape = RoundedCornerShape(8.dp)
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHighest, shape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) { content() }
    } else {
        Box(Modifier.alpha(if (dimmed) 0.6f else 1f)) { content() }
    }
}

/**
 * Casilla cuadrada: naranja con palomita si está completada; solo borde si no.
 * Deshabilitada se ve tenue (todavía no toca esa serie).
 */
@Composable
private fun StatusBox(done: Boolean, enabled: Boolean) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        modifier = Modifier
            .size(36.dp)
            .alpha(if (enabled || done) 1f else 0.35f)
            .then(
                if (done) Modifier.background(BrandOrange, shape)
                else Modifier.border(
                    1.5.dp,
                    if (enabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
                    shape,
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (done) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

// ---------- Previews ----------

@Composable
private fun ExerciseCardPreviewContent(initiallyExpanded: Boolean, darkTheme: Boolean = true) {
    val sets = remember {
        mutableStateListOf(
            ExerciseSetUi(1, 12, done = true),
            ExerciseSetUi(2, 12),
            ExerciseSetUi(3, 10),
        )
    }
    EjercicioTheme(darkTheme = darkTheme, dynamicColor = false) {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            ExerciseCard(
                name = "Press banca inclinado",
                sets = sets,
                thumbnail = R.drawable.ic_musculo_pectoral_superior,
                muscleName = "Pectoral superior",
                equipment = "Mancuernas / 30°",
                initiallyExpanded = initiallyExpanded,
                onSetCheckedChange = { i, done -> sets[i] = sets[i].copy(done = done) },
            )
        }
    }
}

@Preview(name = "Cerrada")
@Composable
private fun ExerciseCardCollapsedPreview() = ExerciseCardPreviewContent(initiallyExpanded = false)

@Preview(name = "Abierta")
@Composable
private fun ExerciseCardExpandedPreview() = ExerciseCardPreviewContent(initiallyExpanded = true)

@Preview(name = "Abierta (claro)")
@Composable
private fun ExerciseCardExpandedLightPreview() =
    ExerciseCardPreviewContent(initiallyExpanded = true, darkTheme = false)
