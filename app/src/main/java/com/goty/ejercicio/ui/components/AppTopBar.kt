package com.goty.ejercicio.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.goty.ejercicio.ui.theme.EjercicioTheme

/** Barra superior: muestra el título de la pantalla actual. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            // Fundido suave del título al cambiar de pestaña
            AnimatedContent(
                targetState = title,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "topBarTitle",
            ) { text ->
                Text(text)
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun AppTopBarPreview() {
    EjercicioTheme {
        AppTopBar(title = "Inicio")
    }
}
