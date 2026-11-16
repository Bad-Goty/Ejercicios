package com.goty.ejercicio.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goty.ejercicio.domain.model.Exercise
import com.goty.ejercicio.domain.usecase.GetTodayExercisesUseCase
import com.goty.ejercicio.domain.usecase.GetStepGoalUseCase
import com.goty.ejercicio.domain.usecase.ObserveRestTimerUseCase
import com.goty.ejercicio.domain.usecase.ObserveTodayCompletedSetsUseCase
import com.goty.ejercicio.domain.usecase.ObserveTodayStepsUseCase
import com.goty.ejercicio.domain.usecase.SetSetCompletedUseCase
import com.goty.ejercicio.domain.usecase.SetStepGoalUseCase
import com.goty.ejercicio.domain.usecase.StartRestTimerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
    /** Series completadas hoy: id del ejercicio -> números de serie hechos (vienen de Room). */
    val completedSets: Map<Long, Set<Int>> = emptyMap(),
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

/** Temporizador de descanso entre series. null = no hay descanso en curso. */
data class RestTimerState(
    val secondsLeft: Int,
    val totalSeconds: Int,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getTodayExercises: GetTodayExercisesUseCase,
    observeTodayCompletedSets: ObserveTodayCompletedSetsUseCase,
    private val setSetCompleted: SetSetCompletedUseCase,
    private val observeTodaySteps: ObserveTodayStepsUseCase,
    getStepGoal: GetStepGoalUseCase,
    private val setStepGoal: SetStepGoalUseCase,
    observeRestTimer: ObserveRestTimerUseCase,
    private val startRestTimer: StartRestTimerUseCase,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> =
        // Solo los ejercicios de las categorías que tocan hoy en Rutinas
        combine(getTodayExercises(), observeTodayCompletedSets()) { exercises, completed ->
            HomeUiState(isLoading = false, exercises = exercises, completedSets = completed)
        }
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

    // --- Descanso entre series ---

    /**
     * Descanso en curso con los segundos que faltan. Sale de lo guardado, así que si se cierra
     * la app y se vuelve a abrir, sigue donde iba. null = no hay descanso.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val restTimer: StateFlow<RestTimerState?> = observeRestTimer()
        .flatMapLatest { timer ->
            if (timer == null) {
                flowOf<RestTimerState?>(null)
            } else {
                flow<RestTimerState?> {
                    while (true) {
                        val left = ((timer.endAtMillis - System.currentTimeMillis() + 999) / 1_000).toInt()
                        if (left <= 0) break
                        emit(RestTimerState(secondsLeft = left, totalSeconds = timer.totalSeconds))
                        delay(200)
                    }
                    emit(null)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    /**
     * Marca o desmarca una serie de hoy. Se guarda en Room, así no se pierde al cerrar la app.
     * Al completarla empieza el descanso.
     */
    fun onSetCheckedChange(exerciseId: Long, setNumber: Int, reps: Int, completed: Boolean) {
        viewModelScope.launch {
            setSetCompleted(exerciseId, setNumber, reps, completed)
            // Empieza el descanso: se guarda y se programa el aviso de fin (aunque se cierre la app)
            if (completed) startRestTimer(REST_SECONDS)
        }
    }

    companion object {
        /** Duración del descanso. Más adelante se podrá configurar. */
        const val REST_SECONDS = 60
    }
}

private val SpanishLocale: Locale = Locale.forLanguageTag("es")

/** Nombre del día actual en español, con mayúscula inicial (p. ej. "Viernes"). */
private fun todayName(): String =
    SimpleDateFormat("EEEE", SpanishLocale)
        .format(Date())
        .replaceFirstChar { it.titlecase(SpanishLocale) }
