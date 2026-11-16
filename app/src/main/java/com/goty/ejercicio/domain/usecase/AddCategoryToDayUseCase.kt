package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.repository.RoutineRepository
import javax.inject.Inject

/** Agrega una categoría a un día de la semana (1 = lunes … 7 = domingo). */
class AddCategoryToDayUseCase @Inject constructor(
    private val repository: RoutineRepository,
) {
    suspend operator fun invoke(dayOfWeek: Int, categoryId: Long) =
        repository.addCategoryToDay(dayOfWeek, categoryId)
}
