package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.model.Category
import com.goty.ejercicio.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Todas las categorías, para elegir cuál agregar a un día de la rutina. */
class GetCategoriesUseCase @Inject constructor(
    private val repository: RoutineRepository,
) {
    operator fun invoke(): Flow<List<Category>> = repository.getCategories()
}
