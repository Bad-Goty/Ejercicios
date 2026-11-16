package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.model.Category
import com.goty.ejercicio.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Categorías que tocan cada día de la semana (1 = lunes … 7 = domingo). */
class ObserveWeekCategoriesUseCase @Inject constructor(
    private val repository: RoutineRepository,
) {
    operator fun invoke(): Flow<Map<Int, List<Category>>> = repository.observeWeekCategories()
}
