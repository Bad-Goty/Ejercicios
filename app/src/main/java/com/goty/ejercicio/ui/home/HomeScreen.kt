package com.goty.ejercicio.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goty.ejercicio.domain.model.Exercise
import com.goty.ejercicio.ui.theme.EjercicioTheme

/** Punto de entrada con ViewModel (stateful). */
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(uiState = uiState)
}

/** UI sin estado: fácil de previsualizar y probar. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(uiState: HomeUiState, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Mis ejercicios") }) },
    ) { innerPadding ->
        when {
            uiState.isLoading -> CenteredBox(innerPadding) { CircularProgressIndicator() }
            uiState.exercises.isEmpty() -> CenteredBox(innerPadding) {
                Text("Aún no hay ejercicios registrados")
            }
            else -> LazyColumn(contentPadding = innerPadding) {
                items(uiState.exercises, key = { it.id }) { exercise ->
                    ListItem(headlineContent = { Text(exercise.name) })
                }
            }
        }
    }
}

@Composable
private fun CenteredBox(padding: PaddingValues, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    EjercicioTheme {
        HomeContent(
            uiState = HomeUiState(
                isLoading = false,
                exercises = listOf(Exercise(1, "Press de banca"), Exercise(2, "Sentadilla")),
            )
        )
    }
}
