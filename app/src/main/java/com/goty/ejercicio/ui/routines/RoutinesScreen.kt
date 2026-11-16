package com.goty.ejercicio.ui.routines

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goty.ejercicio.R
import com.goty.ejercicio.domain.model.Category
import com.goty.ejercicio.ui.navigation.SLIDE_BACKWARD
import com.goty.ejercicio.ui.navigation.SLIDE_FORWARD
import com.goty.ejercicio.ui.navigation.TAB_ANIMATION_MS
import com.goty.ejercicio.ui.navigation.tabSlideTransition
import com.goty.ejercicio.ui.theme.BrandOrange
import com.goty.ejercicio.ui.theme.EjercicioTheme
import kotlin.math.roundToInt

/** Iniciales de lunes (1) a domingo (7). */
private val DayInitials = listOf("L", "M", "M", "J", "V", "S", "D")

/** Nombres de lunes (1) a domingo (7). */
internal val DayNames = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

/** Tamaño del círculo naranja que marca el día elegido. */
private val DayIndicatorSize = 36.dp

/**
 * Sugerencia rápida: agrega de un toque varias categorías al día.
 * Los ids son los fijos de la precarga (PrepopulateCallback).
 */
private data class QuickSuggestion(val emoji: String, val label: String, val categoryIds: List<Long>)

private val QuickSuggestions = listOf(
    QuickSuggestion("💪", "Pecho & Tríceps", listOf(1L, 4L)), // Pecho + Brazos
    QuickSuggestion("⚡", "Cardio & Core", listOf(7L)), // Core (no hay categoría de cardio)
    QuickSuggestion("🦵", "Pierna Completa", listOf(5L, 6L)), // Piernas + Glúteos
)

/** Punto de entrada con ViewModel (stateful). */
@Composable
fun RoutinesScreen(viewModel: RoutinesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RoutinesContent(
        uiState = uiState,
        onDaySelected = viewModel::onDaySelected,
        onAddCategory = viewModel::onAddCategory,
        onAddCategories = viewModel::onAddCategories,
        onRemoveCategory = viewModel::onRemoveCategory,
    )
}

/** UI sin ViewModel: fácil de previsualizar. */
@Composable
fun RoutinesContent(
    uiState: RoutinesUiState,
    onDaySelected: (Int) -> Unit,
    onAddCategory: (categoryId: Long) -> Unit,
    onAddCategories: (categoryIds: List<Long>) -> Unit,
    onRemoveCategory: (day: Int, categoryId: Long) -> Unit,
) {
    var showAddSheet by rememberSaveable { mutableStateOf(false) }
    // Categoría que se mantuvo pulsada y espera confirmación para borrarse
    var pendingDeleteDay by rememberSaveable { mutableStateOf<Int?>(null) }
    var pendingDeleteId by rememberSaveable { mutableStateOf<Long?>(null) }
    val selectedDay = uiState.selectedDay

    val pendingCategory = pendingDeleteDay?.let { day ->
        uiState.categoriesByDay[day]?.firstOrNull { it.id == pendingDeleteId }
    }
    if (pendingCategory != null) {
        val day = pendingDeleteDay!!
        val dismiss = {
            pendingDeleteDay = null
            pendingDeleteId = null
        }
        AlertDialog(
            onDismissRequest = dismiss,
            text = { Text(text = "¿Desea eliminar la categoría ${pendingCategory.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    onRemoveCategory(day, pendingCategory.id)
                    dismiss()
                }) {
                    Text(text = "Eliminar", color = BrandOrange)
                }
            },
            dismissButton = {
                TextButton(onClick = dismiss) {
                    Text(text = "Cancelar")
                }
            },
        )
    }

    if (showAddSheet) {
        val assignedIds = uiState.categoriesByDay[selectedDay].orEmpty().map { it.id }.toSet()
        AddCategoryBottomSheet(
            dayName = DayNames[selectedDay - 1],
            categories = uiState.allCategories.filterNot { it.id in assignedIds },
            onDismiss = { showAddSheet = false },
            onCategoryClick = { category ->
                onAddCategory(category.id)
                showAddSheet = false
            },
        )
    }

    val dotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .dottedBackground(dotColor)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        WeekDaysBar(
            selectedDay = selectedDay,
            daysWithCategories = uiState.categoriesByDay.filterValues { it.isNotEmpty() }.keys,
            onDaySelected = onDaySelected,
        )

        // Al cambiar de día el encabezado y el cuadro se deslizan igual que las pantallas de la
        // BottomBar: un día posterior entra por la derecha y uno anterior por la izquierda.
        AnimatedContent(
            targetState = selectedDay,
            transitionSpec = {
                val direction = if (targetState > initialState) SLIDE_FORWARD else SLIDE_BACKWARD
                tabSlideTransition(direction) using SizeTransform(clip = false)
            },
            label = "dayCategoriesCard",
        ) { day ->
            val categories = uiState.categoriesByDay[day].orEmpty()
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DayHeader(dayName = DayNames[day - 1], categoryCount = categories.size)
                DayCategoriesCard(
                    categories = categories,
                    onAddClick = { showAddSheet = true },
                    onSuggestionClick = { onAddCategories(it.categoryIds) },
                    onCategoryLongClick = { category ->
                        pendingDeleteDay = day
                        pendingDeleteId = category.id
                    },
                )
            }
        }
    }
}

/** Fondo con una cuadrícula de puntos, como en el mockup. */
private fun Modifier.dottedBackground(color: Color): Modifier = drawBehind {
    val step = 18.dp.toPx()
    val radius = 1.dp.toPx()
    var y = step / 2
    while (y < size.height) {
        var x = step / 2
        while (x < size.width) {
            drawCircle(color = color, radius = radius, center = Offset(x, y))
            x += step
        }
        y += step
    }
}

/** "● DOMINGO · ENFOQUE DIARIO" con la etiqueta de estado del día a la derecha. */
@Composable
private fun DayHeader(dayName: String, categoryCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(BrandOrange, CircleShape),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "${dayName.uppercase()} · ENFOQUE DIARIO",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        val status = when (categoryCount) {
            0 -> "Día de descanso activo"
            1 -> "1 categoría"
            else -> "$categoryCount categorías"
        }
        val chipShape = RoundedCornerShape(8.dp)
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, chipShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, chipShape)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

/**
 * Cuadro del día. Sin categorías: título, descripción, "+" y sugerencias rápidas.
 * Con categorías: la lista (mantener pulsado para eliminar) y el "+".
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DayCategoriesCard(
    categories: List<Category>,
    onAddClick: () -> Unit,
    onSuggestionClick: (QuickSuggestion) -> Unit,
    onCategoryLongClick: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(colors.surfaceContainerHigh, colors.surfaceContainer)),
                shape,
            )
            .border(1.dp, colors.outlineVariant, shape)
            .padding(vertical = 28.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (categories.isEmpty()) {
            Text(
                text = "Agrega una categoría",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface,
            )
            Text(
                text = "Organiza tus grupos musculares y bloques de ejercicio para este día.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            AddCategoryButton(onClick = onAddClick)
            Spacer(Modifier.height(12.dp))
            Text(
                text = "SUGERENCIAS RÁPIDAS:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = BrandOrange,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                QuickSuggestions.forEach { suggestion ->
                    SuggestionChip(suggestion = suggestion, onClick = { onSuggestionClick(suggestion) })
                }
            }
        } else {
            categories.forEach { category ->
                // Mantener pulsado para eliminarla del día
                CategoryPill(
                    name = category.name,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { onCategoryLongClick(category) },
                            onLongClickLabel = "Eliminar categoría",
                        ),
                )
            }
            Spacer(Modifier.height(4.dp))
            AddCategoryButton(onClick = onAddClick)
        }
    }
}

/** Chip de sugerencia rápida: "💪 Pecho & Tríceps". */
@Composable
private fun SuggestionChip(suggestion: QuickSuggestion, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = suggestion.emoji, fontSize = 12.sp)
        Spacer(Modifier.width(6.dp))
        Text(
            text = suggestion.label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/** Etiqueta redondeada con el nombre de la categoría. */
@Composable
internal fun CategoryPill(name: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .fillMaxWidth(0.6f)
            .height(44.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/** Botón circular "+" con borde naranja y brillo del mismo color. */
@Composable
private fun AddCategoryButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .shadow(elevation = 12.dp, shape = CircleShape, ambientColor = BrandOrange, spotColor = BrandOrange)
            .background(MaterialTheme.colorScheme.surfaceContainer, CircleShape)
            .border(2.dp, BrandOrange, CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onClick, role = Role.Button),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = "Agregar categoría",
            tint = BrandOrange,
            modifier = Modifier.size(32.dp),
        )
    }
}

/**
 * Barra "L M M J V S D". [selectedDay]: 1 = lunes … 7 = domingo.
 * El círculo naranja se desliza de un día a otro, igual que el indicador de la BottomBar.
 * El punto sobre cada letra es naranja si ese día ya tiene categorías.
 */
@Composable
private fun WeekDaysBar(
    selectedDay: Int,
    daysWithCategories: Set<Int>,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Posición del indicador (0 = lunes). Se lee solo en la fase de layout.
    val indicatorPosition = animateFloatAsState(
        targetValue = (selectedDay - 1).toFloat(),
        animationSpec = tween(durationMillis = TAB_ANIMATION_MS, easing = FastOutSlowInEasing),
        label = "dayIndicator",
    )

    val shape = RoundedCornerShape(16.dp)
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .height(DayIndicatorSize + 10.dp),
    ) {
        val itemWidth = maxWidth / DayInitials.size

        // Círculo que se desliza detrás de las letras (alineado abajo, bajo el punto)
        Box(
            modifier = Modifier
                .offset {
                    val x = itemWidth.toPx() * indicatorPosition.value +
                        (itemWidth - DayIndicatorSize).toPx() / 2
                    IntOffset(x.roundToInt(), 10.dp.roundToPx())
                }
                .size(DayIndicatorSize)
                .background(BrandOrange, CircleShape),
        )

        Row(modifier = Modifier.fillMaxSize()) {
            DayInitials.forEachIndexed { index, initial ->
                val day = index + 1
                DayItem(
                    initial = initial,
                    selected = day == selectedDay,
                    hasCategories = day in daysWithCategories,
                    onClick = { onDaySelected(day) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
            }
        }
    }
}

@Composable
private fun DayItem(
    initial: String,
    selected: Boolean,
    hasCategories: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val content by animateColorAsState(
        targetValue = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(TAB_ANIMATION_MS),
        label = "dayContent",
    )
    Column(
        modifier = modifier.selectable(
            selected = selected,
            // Sin sombreado al tocar: el círculo deslizante ya marca la selección
            interactionSource = null,
            indication = null,
            role = Role.Tab,
            onClick = onClick,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .background(
                    if (hasCategories) BrandOrange else MaterialTheme.colorScheme.outlineVariant,
                    CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(DayIndicatorSize),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initial,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = content,
            )
        }
    }
}

private val PreviewCategories = mapOf(
    1 to listOf(Category(1, "Pecho"), Category(4, "Brazos")),
    3 to listOf(Category(5, "Piernas")),
)

@Preview(name = "Oscuro · día vacío", heightDp = 700)
@Composable
private fun RoutinesContentDarkPreview() {
    EjercicioTheme(darkTheme = true, dynamicColor = false) {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            RoutinesContent(
                uiState = RoutinesUiState(selectedDay = 7, categoriesByDay = PreviewCategories),
                onDaySelected = {},
                onAddCategory = {},
                onAddCategories = {},
                onRemoveCategory = { _, _ -> },
            )
        }
    }
}

@Preview(name = "Claro · con categorías", heightDp = 700)
@Composable
private fun RoutinesContentLightPreview() {
    EjercicioTheme(darkTheme = false, dynamicColor = false) {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            RoutinesContent(
                uiState = RoutinesUiState(selectedDay = 1, categoriesByDay = PreviewCategories),
                onDaySelected = {},
                onAddCategory = {},
                onAddCategories = {},
                onRemoveCategory = { _, _ -> },
            )
        }
    }
}
