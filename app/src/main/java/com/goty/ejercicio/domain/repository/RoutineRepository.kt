package com.goty.ejercicio.domain.repository

import com.goty.ejercicio.domain.model.Category
import kotlinx.coroutines.flow.Flow

/** Contrato del repositorio de rutinas. La capa data lo implementa. */
interface RoutineRepository {

    /** Categorías de cada día de la semana (1 = lunes … 7 = domingo). Los días sin categorías no aparecen. */
    fun observeWeekCategories(): Flow<Map<Int, List<Category>>>

    /** Todas las categorías, en el orden de la precarga. */
    fun getCategories(): Flow<List<Category>>

    /** Agrega una categoría a un día. Si ya estaba, no hace nada. */
    suspend fun addCategoryToDay(dayOfWeek: Int, categoryId: Long)

    /** Quita una categoría de un día. Si no estaba, no hace nada. */
    suspend fun removeCategoryFromDay(dayOfWeek: Int, categoryId: Long)
}
