package com.goty.ejercicio.ui.addexercise

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.goty.ejercicio.R
import com.goty.ejercicio.domain.model.Muscle
import com.goty.ejercicio.ui.theme.BrandOrange
import com.goty.ejercicio.ui.theme.EjercicioTheme

/** Naranja más claro para el degradado del botón Guardar. */
private val BrandOrangeLight = Color(0xFFFF9100)

/** Punto de entrada con ViewModel (stateful). */
@Composable
fun AddExerciseScreen(viewModel: AddExerciseViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Mensaje al guardar (o si falla)
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            val message = when (event) {
                AddExerciseEvent.Saved -> "Ejercicio guardado"
                is AddExerciseEvent.Error -> event.message
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Selector de fotos del sistema: solo imágenes (incluye GIF). No requiere permisos.
    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri -> uri?.let { viewModel.onImagePicked(it.toString()) } }

    AddExerciseContent(
        uiState = uiState,
        onNameChange = viewModel::onNameChange,
        onMuscleSelected = viewModel::onMuscleSelected,
        onSeriesChange = viewModel::onSeriesChange,
        onRepsChange = viewModel::onRepsChange,
        onPickImage = {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onRemoveImage = viewModel::onImageRemoved,
        onSaveClick = viewModel::onSaveClick,
    )
}

/** UI sin ViewModel: fácil de previsualizar. */
@Composable
fun AddExerciseContent(
    uiState: AddExerciseUiState,
    onNameChange: (String) -> Unit,
    onMuscleSelected: (Muscle) -> Unit,
    onSeriesChange: (Int) -> Unit,
    onRepsChange: (Int) -> Unit,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    onSaveClick: () -> Unit,
) {
    var showCategorySheet by rememberSaveable { mutableStateOf(false) }
    val form = uiState.form

    if (showCategorySheet) {
        CategoryBottomSheet(
            categories = uiState.categories,
            selectedMuscleId = form.selectedMuscle?.id,
            onDismiss = { showCategorySheet = false },
            onMuscleClick = { muscle ->
                onMuscleSelected(muscle)
                showCategorySheet = false
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                vertical = 8.dp,
                horizontal = 16.dp,
            ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- Nombre ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionHeader(title = "Nombre del ejercicio", trailing = "requerido", trailingColor = BrandOrange)
            OutlinedTextField(
                value = form.name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = form.showNameError,
                supportingText = if (form.showNameError) {
                    { Text(text = "Escribe el nombre del ejercicio") }
                } else null,
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text(text = "Press banca inclinado") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_fitness_center),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandOrange,
                    focusedLabelColor = BrandOrange,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        }

        // --- Categoría (sin cambios) ---
        LabeledSection(label = "Categoría") {
            OutlinedButton(
                onClick = { showCategorySheet = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                // Muestra el músculo elegido; si aún no hay, el "+"
                Text(text = form.selectedMuscle?.name ?: "+", color = BrandOrange)
            }
        }

        // --- Entrenamiento ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionHeader(title = "Entrenamiento")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StepperCard(
                    label = "Series",
                    value = form.series,
                    onDecrease = { onSeriesChange(form.series - 1) },
                    onIncrease = { onSeriesChange(form.series + 1) },
                    modifier = Modifier.weight(1f),
                )
                StepperCard(
                    label = "Repeticiones (Repe)",
                    value = form.reps,
                    onDecrease = { onRepsChange(form.reps - 1) },
                    onIncrease = { onRepsChange(form.reps + 1) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // --- Imagen ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionHeader(title = "Imagen / Demostración", trailing = "Opcional")
            ImageUploadBox(
                imageUri = form.imageUri,
                onBrowseClick = onPickImage,
                onRemoveClick = onRemoveImage,
            )
        }

        SaveButton(onClick = onSaveClick, enabled = !form.isSaving)
    }
}

/** Título de sección en mayúsculas con un texto opcional a la derecha. */
@Composable
private fun SectionHeader(
    title: String,
    trailing: String? = null,
    trailingColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = trailingColor,
            )
        }
    }
}

/** Tarjeta con un contador "−  N  +". */
@Composable
private fun StepperCard(
    label: String,
    value: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainer, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(12.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            StepperIcon(R.drawable.ic_remove, contentDescription = "Restar $label", onClick = onDecrease)
            Text(
                text = value.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            StepperIcon(R.drawable.ic_add, contentDescription = "Sumar $label", onClick = onIncrease)
        }
    }
}

@Composable
private fun StepperIcon(@DrawableRes icon: Int, contentDescription: String, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/**
 * Recuadro para la foto/GIF: si ya hay una elegida muestra la vista previa (GIF animado)
 * con un botón para quitarla; si no, el recuadro punteado para subir una.
 */
@Composable
private fun ImageUploadBox(
    imageUri: String?,
    onBrowseClick: () -> Unit,
    onRemoveClick: () -> Unit,
) {
    if (imageUri != null) {
        ImagePreview(imageUri = imageUri, onChangeClick = onBrowseClick, onRemoveClick = onRemoveClick)
        return
    }
    val dashColor = MaterialTheme.colorScheme.outline
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = dashColor,
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)),
                    ),
                )
            }
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onBrowseClick)
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(BrandOrange.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_camera),
                contentDescription = null,
                tint = BrandOrange,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Sube una foto o GIF demostrativo",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "PNG, JPG o GIF hasta 10 MB",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onBrowseClick,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Text(text = "Explorar archivos", style = MaterialTheme.typography.labelMedium)
        }
    }
}

/** Vista previa de la imagen elegida. Tocarla permite cambiarla; la X la quita. */
@Composable
private fun ImagePreview(
    imageUri: String,
    onChangeClick: () -> Unit,
    onRemoveClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onChangeClick),
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "Imagen del ejercicio",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(36.dp)
                .background(Color.Black.copy(alpha = 0.55f), CircleShape),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "Quitar imagen",
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/** Botón principal con degradado naranja y sombra del mismo color. */
@Composable
private fun SaveButton(onClick: () -> Unit, enabled: Boolean = true) {
    val shape = RoundedCornerShape(16.dp)
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(elevation = 10.dp, shape = shape, ambientColor = BrandOrange, spotColor = BrandOrange)
            .background(Brush.horizontalGradient(listOf(BrandOrange, BrandOrangeLight)), shape),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White.copy(alpha = 0.7f),
        ),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "GUARDAR EJERCICIO",
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
        )
    }
}

@Preview(name = "Oscuro", heightDp = 900)
@Composable
private fun AddExerciseContentDarkPreview() {
    EjercicioTheme(darkTheme = true, dynamicColor = false) {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            AddExerciseContent(
                uiState = AddExerciseUiState(),
                onNameChange = {},
                onMuscleSelected = {},
                onSeriesChange = {},
                onRepsChange = {},
                onPickImage = {},
                onRemoveImage = {},
                onSaveClick = {},
            )
        }
    }
}

@Preview(name = "Claro", heightDp = 900)
@Composable
private fun AddExerciseContentLightPreview() {
    EjercicioTheme(darkTheme = false, dynamicColor = false) {
        Box(Modifier.background(MaterialTheme.colorScheme.background)) {
            AddExerciseContent(
                uiState = AddExerciseUiState(),
                onNameChange = {},
                onMuscleSelected = {},
                onSeriesChange = {},
                onRepsChange = {},
                onPickImage = {},
                onRemoveImage = {},
                onSaveClick = {},
            )
        }
    }
}

/**
 * Contenedor con borde redondeado y, opcionalmente, un título encima del borde
 * (igual que el label de un OutlinedTextField).
 */
@Composable
private fun LabeledSection(
    modifier: Modifier = Modifier,
    label: String? = null,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            content()
        }
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 4.dp)
            )
        }
    }
}
