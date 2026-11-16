package com.goty.ejercicio.ui.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goty.ejercicio.domain.model.Category
import com.goty.ejercicio.domain.model.todayDayOfWeek
import com.goty.ejercicio.domain.usecase.AddCategoryToDayUseCase
import com.goty.ejercicio.domain.usecase.GetCategoriesUseCase
import com.goty.ejercicio.domain.usecase.ObserveWeekCategoriesUseCase
import com.goty.ejercicio.domain.usecase.RemoveCategoryFromDayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoutinesUiState(
    /** Día elegido: 1 = lunes … 7 = domingo. */
    val selectedDay: Int,
    /** Categorías de cada día. Se tienen todas para que el cuadro que sale en la animación muestre las suyas. */
    val categoriesByDay: Map<Int, List<Category>> = emptyMap(),
    /** Todas las categorías, para la hoja de "Agregar categoría". */
    val allCategories: List<Category> = emptyList(),
)

@HiltViewModel
class RoutinesViewModel @Inject constructor(
    observeWeekCategories: ObserveWeekCategoriesUseCase,
    getCategories: GetCategoriesUseCase,
    private val addCategoryToDay: AddCategoryToDayUseCase,
    private val removeCategoryFromDay: RemoveCategoryFromDayUseCase,
) : ViewModel() {

    // Al entrar por primera vez se selecciona el día en curso
    private val selectedDay = MutableStateFlow(todayDayOfWeek())

    val uiState: StateFlow<RoutinesUiState> = combine(
        selectedDay,
        observeWeekCategories(),
        getCategories(),
    ) { day, byDay, all ->
        RoutinesUiState(selectedDay = day, categoriesByDay = byDay, allCategories = all)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RoutinesUiState(selectedDay = selectedDay.value),
    )

    fun onDaySelected(day: Int) {
        selectedDay.value = day
    }

    /** Agrega la categoría al día que está seleccionado. */
    fun onAddCategory(categoryId: Long) {
        val day = selectedDay.value
        viewModelScope.launch { addCategoryToDay(day, categoryId) }
    }

    /** Agrega varias categorías al día seleccionado (sugerencias rápidas). Las que ya tenía se ignoran. */
    fun onAddCategories(categoryIds: List<Long>) {
        val day = selectedDay.value
        viewModelScope.launch { categoryIds.forEach { addCategoryToDay(day, it) } }
    }

    /** Quita la categoría del día indicado (el que se confirmó en el diálogo). */
    fun onRemoveCategory(day: Int, categoryId: Long) {
        viewModelScope.launch { removeCategoryFromDay(day, categoryId) }
    }
}
