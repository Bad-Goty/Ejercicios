package com.goty.ejercicio.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goty.ejercicio.domain.model.Exercise
import com.goty.ejercicio.domain.usecase.GetExercisesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val exercises: List<Exercise> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getExercises: GetExercisesUseCase,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = getExercises()
        .map { HomeUiState(isLoading = false, exercises = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )
}
