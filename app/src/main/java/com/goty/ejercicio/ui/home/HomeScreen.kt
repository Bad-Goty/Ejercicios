package com.goty.ejercicio.ui.home

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.goty.ejercicio.ui.components.ExerciseCard
import com.goty.ejercicio.ui.components.ExerciseSetUi
import com.goty.ejercicio.ui.components.muscleDrawable
import com.goty.ejercicio.ui.theme.EjercicioTheme

/** Punto de entrada con ViewModel (stateful). */
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val stepsState by viewModel.stepsState.collectAsStateWithLifecycle()
    val restTimer by viewModel.restTimer.collectAsStateWithLifecycle()
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

    // --- Permisos para el aviso del descanso (se piden al completar la primera serie) ---
    var restPermissionsAsked by rememberSaveable { mutableStateOf(false) }
    var showExactAlarmDialog by rememberSaveable { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        // Después de las notificaciones, el permiso de alarma exacta (si falta)
        if (needsExactAlarmPermission(context)) showExactAlarmDialog = true
    }

    fun askRestPermissionsOnce() {
        if (restPermissionsAsked) return
        restPermissionsAsked = true
        when {
            needsNotificationPermission(context) ->
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            needsExactAlarmPermission(context) -> showExactAlarmDialog = true
        }
    }

    HomeContent(
        uiState = uiState,
        stepsState = stepsState,
        onGoalClick = { showGoalDialog = true },
        onOpenAppSettings = { openAppSettings(context) },
        restTimer = restTimer,
        onSetCheckedChange = { exerciseId, setNumber, reps, completed ->
            viewModel.onSetCheckedChange(exerciseId, setNumber, reps, completed)
            if (completed) askRestPermissionsOnce()
        },
    )

    if (showExactAlarmDialog) {
        AlertDialog(
            onDismissRequest = { showExactAlarmDialog = false },
            title = { Text("Aviso al terminar el descanso") },
            text = {
                Text(
                    "Para que el teléfono vibre justo cuando termine el descanso, aunque cierres " +
                        "la app, activa «Alarmas y recordatorios» para Ejercicio."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showExactAlarmDialog = false
                    openExactAlarmSettings(context)
                }) { Text("Activar") }
            },
            dismissButton = {
                TextButton(onClick = { showExactAlarmDialog = false }) { Text("Ahora no") }
            },
        )
    }

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
    restTimer: RestTimerState? = null,
    onSetCheckedChange: (exerciseId: Long, setNumber: Int, reps: Int, completed: Boolean) -> Unit =
        { _, _, _, _ -> },
) {
  Box(modifier = modifier.fillMaxSize()) {
    // Una sola lista: el contador de pasos es el primer elemento, así se desplaza con los ejercicios.
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "header") {
            HomeHeader(
                dayName = stepsState.dayName,
                steps = stepsState.steps,
                goal = stepsState.goal,
                onGoalClick = onGoalClick,
                modifier = Modifier.padding(bottom = 4.dp),
                notice = stepsNotice(stepsState.status, onOpenAppSettings),
            )
        }

        when {
            uiState.isLoading -> item(key = "loading") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center,
                ) { CircularProgressIndicator() }
            }

            uiState.exercises.isEmpty() -> item(key = "empty") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("Hoy no hay ejercicios en tu rutina") }
            }

            else -> items(uiState.exercises, key = { it.id }) { exercise ->
                // Series hechas hoy (guardadas en Room)
                val doneToday = uiState.completedSets[exercise.id].orEmpty()
                ExerciseCard(
                    name = exercise.name,
                    sets = List(exercise.series) { i ->
                        ExerciseSetUi(number = i + 1, reps = exercise.reps, done = (i + 1) in doneToday)
                    },
                    onSetCheckedChange = { index, checked ->
                        onSetCheckedChange(exercise.id, index + 1, exercise.reps, checked)
                    },
                    thumbnail = muscleDrawable(exercise.mainMusclePhoto),
                    muscleName = exercise.mainMuscleName,
                    equipment = exercise.equipment,
                    photo = exercise.photo,
                )
            }
        }
    }

    // Descanso entre series: cubre y bloquea Inicio hasta que termine
    AnimatedVisibility(visible = restTimer != null, enter = fadeIn(), exit = fadeOut()) {
        // Conserva el último valor mientras se desvanece
        var lastState by remember { mutableStateOf(restTimer) }
        if (restTimer != null) lastState = restTimer
        lastState?.let { RestTimerOverlay(it) }
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

private fun needsNotificationPermission(context: Context): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
        PackageManager.PERMISSION_GRANTED

private fun needsExactAlarmPermission(context: Context): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        !context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()

/** Abre la pantalla del sistema "Alarmas y recordatorios" de esta app. */
private fun openExactAlarmSettings(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    val intent = Intent(
        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
        Uri.fromParts("package", context.packageName, null),
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

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
                exercises = listOf(
                    Exercise(1, "Press de banca", series = 4, reps = 10, mainMusclePhoto = "ic_musculo_pectoral_medio"),
                    Exercise(2, "Sentadilla", series = 3, reps = 12, mainMusclePhoto = "ic_musculo_recto_femoral"),
                ),
            ),
            stepsState = StepsUiState(dayName = "Viernes", steps = 350, goal = 1000),
            onGoalClick = {},
            onOpenAppSettings = {},
        )
    }
}
