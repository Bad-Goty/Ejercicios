package com.goty.ejercicio.ui.home

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.goty.ejercicio.ui.theme.EjercicioTheme

private const val MAX_STEP_GOAL = 100_000

/** Diálogo para cambiar la meta diaria de pasos. */
@Composable
fun StepGoalDialog(
    currentGoal: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(currentGoal.toString()) }
    val goal = text.toIntOrNull()
    val isValid = goal != null && goal in 1..MAX_STEP_GOAL

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Meta de pasos") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { newText -> text = newText.filter(Char::isDigit).take(6) },
                label = { Text("Pasos por día") },
                singleLine = true,
                isError = !isValid,
                supportingText = {
                    if (!isValid) Text("Escribe un número entre 1 y $MAX_STEP_GOAL")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (goal != null && isValid) onConfirm(goal) },
                enabled = isValid,
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}

@Preview
@Composable
private fun StepGoalDialogPreview() {
    EjercicioTheme {
        StepGoalDialog(currentGoal = 1000, onConfirm = {}, onDismiss = {})
    }
}
