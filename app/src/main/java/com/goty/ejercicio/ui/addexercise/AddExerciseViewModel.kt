package com.goty.ejercicio.ui.addexercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goty.ejercicio.domain.model.Muscle
import com.goty.ejercicio.domain.model.MuscleCategory
import com.goty.ejercicio.domain.usecase.GetMuscleCategoriesUseCase
import com.goty.ejercicio.domain.usecase.SaveExerciseUseCase
import com.goty.ejercicio.domain.usecase.ValidateExerciseImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Datos que escribe el usuario en el formulario. */
data class AddExerciseForm(
    val name: String = "",
    val selectedMuscle: Muscle? = null,
    val series: Int = 4,
    val reps: Int = 12,
    /** URI de la foto/GIF elegida con el selector (aún no copiada). */
    val imageUri: String? = null,
    /** true si intentó guardar sin nombre. */
    val showNameError: Boolean = false,
    val isSaving: Boolean = false,
)

data class AddExerciseUiState(
    val categories: List<MuscleCategory> = emptyList(),
    val form: AddExerciseForm = AddExerciseForm(),
)

/** Eventos de una sola vez para la pantalla. */
sealed interface AddExerciseEvent {
    data object Saved : AddExerciseEvent
    data class Error(val message: String) : AddExerciseEvent
}

@HiltViewModel
class AddExerciseViewModel @Inject constructor(
    getMuscleCategories: GetMuscleCategoriesUseCase,
    private val saveExercise: SaveExerciseUseCase,
    private val validateImage: ValidateExerciseImageUseCase,
) : ViewModel() {

    private val form = MutableStateFlow(AddExerciseForm())

    private val _events = Channel<AddExerciseEvent>(Channel.BUFFERED)
    val events: Flow<AddExerciseEvent> = _events.receiveAsFlow()

    val uiState: StateFlow<AddExerciseUiState> =
        combine(getMuscleCategories(), form) { categories, form ->
            AddExerciseUiState(categories = categories, form = form)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AddExerciseUiState(),
        )

    fun onNameChange(name: String) {
        form.update { it.copy(name = name, showNameError = false) }
    }

    fun onMuscleSelected(muscle: Muscle) {
        form.update { it.copy(selectedMuscle = muscle) }
    }

    fun onSeriesChange(series: Int) {
        if (series >= 1) form.update { it.copy(series = series) }
    }

    fun onRepsChange(reps: Int) {
        if (reps >= 1) form.update { it.copy(reps = reps) }
    }

    fun onImagePicked(uri: String) {
        viewModelScope.launch {
            if (validateImage(uri)) {
                form.update { it.copy(imageUri = uri) }
            } else {
                _events.send(AddExerciseEvent.Error("La imagen pesa más de 10 MB"))
            }
        }
    }

    fun onImageRemoved() {
        form.update { it.copy(imageUri = null) }
    }

    fun onSaveClick() {
        val current = form.value
        if (current.isSaving) return
        if (current.name.isBlank()) {
            form.update { it.copy(showNameError = true) }
            return
        }
        form.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            runCatching { saveExercise(current.name, current.selectedMuscle?.id, current.imageUri) }
                .onSuccess {
                    form.value = AddExerciseForm() // limpia el formulario
                    _events.send(AddExerciseEvent.Saved)
                }
                .onFailure { error ->
                    form.update { it.copy(isSaving = false) }
                    _events.send(AddExerciseEvent.Error(error.message ?: "No se pudo guardar"))
                }
        }
    }
}
