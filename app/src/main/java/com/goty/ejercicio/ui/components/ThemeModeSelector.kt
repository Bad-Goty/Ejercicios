package com.goty.ejercicio.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goty.ejercicio.domain.model.ThemeMode
import com.goty.ejercicio.ui.theme.EjercicioTheme

/** Selector de tema: Sistema / Claro / Oscuro. Pensado para moverse a Configuraciones. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeModeSelector(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = ThemeMode.entries
    Column(modifier = modifier) {
        Text(
            text = "Tema",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = mode == selected,
                    onClick = { onSelect(mode) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                ) {
                    Text(mode.label())
                }
            }
        }
    }
}

private fun ThemeMode.label(): String = when (this) {
    ThemeMode.SYSTEM -> "Sistema"
    ThemeMode.LIGHT -> "Claro"
    ThemeMode.DARK -> "Oscuro"
}

@Preview(showBackground = true)
@Composable
private fun ThemeModeSelectorPreview() {
    EjercicioTheme {
        ThemeModeSelector(selected = ThemeMode.SYSTEM, onSelect = {}, modifier = Modifier.padding(16.dp))
    }
}
