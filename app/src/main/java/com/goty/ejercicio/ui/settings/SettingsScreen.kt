package com.goty.ejercicio.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goty.ejercicio.domain.model.ThemeMode
import com.goty.ejercicio.ui.components.ThemeModeSelector
import com.goty.ejercicio.ui.theme.EjercicioTheme

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    SettingsContent(
        themeMode = themeMode,
        onThemeModeChange = viewModel::onThemeModeChange,
    )
}

@Composable
fun SettingsContent(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        ThemeModeSelector(
            selected = themeMode,
            onSelect = onThemeModeChange,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsContentPreview() {
    EjercicioTheme {
        SettingsContent(themeMode = ThemeMode.SYSTEM, onThemeModeChange = {})
    }
}
