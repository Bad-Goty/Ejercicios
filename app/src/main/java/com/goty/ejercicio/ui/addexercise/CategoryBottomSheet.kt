package com.goty.ejercicio.ui.addexercise

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.goty.ejercicio.domain.model.Muscle
import com.goty.ejercicio.domain.model.MuscleCategory
import com.goty.ejercicio.ui.components.muscleDrawable
import com.goty.ejercicio.ui.theme.BrandOrange

/**
 * Hoja inferior con cada categoría (título) y sus músculos debajo (imagen + nombre) para elegir uno.
 * El músculo seleccionado se marca con borde naranja.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBottomSheet(
    categories: List<MuscleCategory>,
    selectedMuscleId: Long?,
    onDismiss: () -> Unit,
    onMuscleClick: (Muscle) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Text(
            text = "Categoría",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            categories.forEach { category ->
                // Título de la categoría: ocupa todo el ancho
                item(
                    key = "categoria_${category.id}",
                    span = { GridItemSpan(maxLineSpan) },
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = BrandOrange,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(category.muscles, key = { "musculo_${it.id}" }) { muscle ->
                    MuscleCard(
                        muscle = muscle,
                        selected = muscle.id == selectedMuscleId,
                        onClick = { onMuscleClick(muscle) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MuscleCard(
    muscle: Muscle,
    selected: Boolean,
    onClick: () -> Unit,
) {
    OutlinedCard(
        onClick = onClick,
        border = if (selected) BorderStroke(2.dp, BrandOrange) else CardDefaults.outlinedCardBorder(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        ) {
            muscleDrawable(muscle.photo)?.let { res ->
                Image(
                    painter = painterResource(res),
                    contentDescription = null, // el nombre ya se lee en el Text
                    modifier = Modifier.size(width = 60.dp, height = 120.dp),
                )
            }
            Text(
                text = muscle.name,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = if (selected) BrandOrange else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
