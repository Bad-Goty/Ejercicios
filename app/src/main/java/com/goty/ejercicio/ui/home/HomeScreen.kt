package com.goty.ejercicio.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
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

/** UI sin estado: fácil de previsualizar y probar. La TopBar y BottomBar las pone AppNavigation. */
@Composable
fun HomeContent(uiState: HomeUiState, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when {
            uiState.isLoading -> CircularProgressIndicator()
            uiState.exercises.isEmpty() -> Text("Aún no hay ejercicios registrados")
            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.exercises, key = { it.id }) { exercise ->
                    ListItem(headlineContent = { Text(exercise.name) })
                }
            }
        }
    }
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
