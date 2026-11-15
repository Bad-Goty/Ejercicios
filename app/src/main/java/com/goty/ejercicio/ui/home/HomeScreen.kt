package com.goty.ejercicio.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.goty.ejercicio.domain.model.Exercise
import com.goty.ejercicio.ui.theme.EjercicioTheme

/** Punto de entrada con ViewModel (stateful). */
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val stepsState by viewModel.stepsState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showGoalDialog by rememberSaveable { mutableStateOf(false) }
    var permissionRequested by rememberSaveable { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> viewModel.onActivityPermissionResult(granted) }

    // Revisa el permiso cada vez que la pantalla vuelve a primer plano
    // (p. ej. al regresar de los ajustes del sistema). Solo se pide automáticamente una vez.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        when {
            hasActivityRecognitionPermission(context) -> viewModel.onActivityPermissionResult(true)
            !permissionRequested -> {
                permissionRequested = true
                permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
            else -> viewModel.onActivityPermissionResult(false)
        }
    }

    HomeContent(
        uiState = uiState,
        stepsState = stepsState,
        onGoalClick = { showGoalDialog = true },
        onOpenAppSettings = { openAppSettings(context) },
    )

    if (showGoalDialog) {
        StepGoalDialog(
            currentGoal = stepsState.goal,
            onConfirm = { goal ->
                viewModel.onStepGoalChange(goal)
                showGoalDialog = false
            },
            onDismiss = { showGoalDialog = false },
        )
    }
}

/** UI sin estado: fácil de previsualizar y probar. La TopBar y BottomBar las pone AppNavigation. */
@Composable
fun HomeContent(
    uiState: HomeUiState,
    stepsState: StepsUiState,
    onGoalClick: () -> Unit,
    onOpenAppSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        HomeHeader(
            dayName = stepsState.dayName,
            steps = stepsState.steps,
            goal = stepsState.goal,
            onGoalClick = onGoalClick,
            modifier = Modifier.padding(16.dp),
            notice = stepsNotice(stepsState.status, onOpenAppSettings),
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
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
}

/** Aviso para el encabezado cuando no se pueden contar pasos; null si todo está bien. */
private fun stepsNotice(
    status: StepTrackingStatus,
    onOpenAppSettings: () -> Unit,
): (@Composable () -> Unit)? = when (status) {
    StepTrackingStatus.TRACKING -> null
    StepTrackingStatus.SENSOR_UNAVAILABLE -> {
        {
            Text(
                text = "Este dispositivo no tiene sensor de pasos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
    StepTrackingStatus.PERMISSION_DENIED -> {
        {
            Column {
                Text(
                    text = "Para contar tus pasos, permite el acceso a \"Actividad física\".",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = onOpenAppSettings) {
                    Text("Abrir ajustes")
                }
            }
        }
    }
}

private fun hasActivityRecognitionPermission(context: Context): Boolean =
    // Antes de Android 10 el sensor de pasos no necesita permiso
    Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) ==
        PackageManager.PERMISSION_GRANTED

private fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null),
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    EjercicioTheme {
        HomeContent(
            uiState = HomeUiState(
                isLoading = false,
                exercises = listOf(Exercise(1, "Press de banca"), Exercise(2, "Sentadilla")),
            ),
            stepsState = StepsUiState(dayName = "Viernes", steps = 350, goal = 1000),
            onGoalClick = {},
            onOpenAppSettings = {},
        )
    }
}
