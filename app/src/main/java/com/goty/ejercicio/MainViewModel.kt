package com.goty.ejercicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goty.ejercicio.domain.model.ThemeMode
import com.goty.ejercicio.domain.usecase.GetThemeModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Estado global de la Activity (por ahora, el tema de la app). */
@HiltViewModel
class MainViewModel @Inject constructor(
    getThemeMode: GetThemeModeUseCase,
) : ViewModel() {

    /** null mientras se lee la preferencia guardada. */
    val themeMode: StateFlow<ThemeMode?> = getThemeMode()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )
}
