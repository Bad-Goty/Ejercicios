package com.goty.ejercicio.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goty.ejercicio.domain.model.Exercise
import com.goty.ejercicio.domain.usecase.GetExercisesUseCase
import com.goty.ejercicio.domain.usecase.GetStepGoalUseCase
import com.goty.ejercicio.domain.usecase.ObserveTodayStepsUseCase
import com.goty.ejercicio.domain.usecase.SetStepGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val exercises: List<Exercise> = emptyList(),
)

enum class StepTrackingStatus {
    /** Contando pasos (o esperando saber si hay permiso). */
    TRACKING,

    /** El usuario no dio el permiso de Actividad física. */
    PERMISSION_DENIED,

    /** El dispositivo no tiene sensor de pasos. */
    SENSOR_UNAVAILABLE,
}

data class StepsUiState(
    val dayName: String = "",
    val steps: Int = 0,
    val goal: Int = 0,
    val status: StepTrackingStatus = StepTrackingStatus.TRACKING,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getExercises: GetExercisesUseCase,
    private val observeTodaySteps: ObserveTodayStepsUseCase,
    getStepGoal: GetStepGoalUseCase,
    private val setStepGoal: SetStepGoalUseCase,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = getExercises()
        .map { HomeUiState(isLoading = false, exercises = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )

    /** null mientras no se sabe si hay permiso de Actividad física. */
    private val hasActivityPermission = MutableStateFlow<Boolean?>(null)

    /**
     * Encabezado: día, pasos y meta. El sensor solo se escucha mientras la
     * pantalla está visible y hay permiso; al obtener el permiso se reinicia.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val stepsState: StateFlow<StepsUiState> = hasActivityPermission
        .flatMapLatest { granted ->
            combine(
                observeTodaySteps(trackWithSensor = granted == true),
                getStepGoal(),
            ) { steps, goal ->
                StepsUiState(
                    dayName = todayName(),
                    steps = steps,
                    goal = goal,
                    status = when {
                        !observeTodaySteps.isStepSensorAvailable -> StepTrackingStatus.SENSOR_UNAVAILABLE
                        granted == false -> StepTrackingStatus.PERMISSION_DENIED
                        else -> StepTrackingStatus.TRACKING
                    },
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StepsUiState(dayName = todayName()),
        )

    fun onActivityPermissionResult(granted: Boolean) {
        hasActivityPermission.value = granted
    }

    fun onStepGoalChange(goal: Int) {
        viewModelScope.launch { setStepGoal(goal) }
    }
}

private val SpanishLocale: Locale = Locale.forLanguageTag("es")

/** Nombre del día actual en español, con mayúscula inicial (p. ej. "Viernes"). */
private fun todayName(): String =
    SimpleDateFormat("EEEE", SpanishLocale)
        .format(Date())
        .replaceFirstChar { it.titlecase(SpanishLocale) }
