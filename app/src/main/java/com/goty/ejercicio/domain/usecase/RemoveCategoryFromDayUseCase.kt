package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.repository.RoutineRepository
import javax.inject.Inject

/** Quita una categoría de un día de la semana (1 = lunes … 7 = domingo). */
class RemoveCategoryFromDayUseCase @Inject constructor(
    private val repository: RoutineRepository,
) {
    suspend operator fun invoke(dayOfWeek: Int, categoryId: Long) =
        repository.removeCategoryFromDay(dayOfWeek, categoryId)
}
