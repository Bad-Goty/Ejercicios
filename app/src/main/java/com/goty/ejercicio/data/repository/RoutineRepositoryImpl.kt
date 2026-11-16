package com.goty.ejercicio.data.repository

import com.goty.ejercicio.data.local.dao.CategoriaDao
import com.goty.ejercicio.data.local.dao.RutinaDao
import com.goty.ejercicio.data.local.entity.Rutina
import com.goty.ejercicio.data.local.entity.RutinaDia
import com.goty.ejercicio.data.mapper.toDomain
import com.goty.ejercicio.domain.model.Category
import com.goty.ejercicio.domain.repository.RoutineRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Por ahora la app maneja una sola rutina: la primera de la tabla `rutinas`.
 * Si todavía no existe, se crea al agregar la primera categoría.
 */
class RoutineRepositoryImpl @Inject constructor(
    private val rutinaDao: RutinaDao,
    private val categoriaDao: CategoriaDao,
) : RoutineRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeWeekCategories(): Flow<Map<Int, List<Category>>> =
        rutinaDao.observarPrimeraId().flatMapLatest { rutinaId ->
            if (rutinaId == null) {
                flowOf(emptyMap())
            } else {
                // obtenerConDias ya viene ordenado por día y categoría
                rutinaDao.obtenerConDias(rutinaId).map { rutina ->
                    rutina?.dias.orEmpty().groupBy(
                        keySelector = { it.rutinaDia.diaSemana },
                        valueTransform = { it.categoria.toDomain() },
                    )
                }
            }
        }

    override fun getCategories(): Flow<List<Category>> =
        categoriaDao.obtenerTodas().map { list -> list.sortedBy { it.id }.map { it.toDomain() } }

    override suspend fun addCategoryToDay(dayOfWeek: Int, categoryId: Long) {
        val rutinaId = rutinaDao.obtenerPrimeraId()
            ?: rutinaDao.insertar(Rutina(nombre = DEFAULT_ROUTINE_NAME))
        rutinaDao.asignarCategoria(
            RutinaDia(rutinaId = rutinaId, diaSemana = dayOfWeek, categoriaId = categoryId),
        )
    }

    override suspend fun removeCategoryFromDay(dayOfWeek: Int, categoryId: Long) {
        val rutinaId = rutinaDao.obtenerPrimeraId() ?: return
        rutinaDao.quitarCategoria(
            RutinaDia(rutinaId = rutinaId, diaSemana = dayOfWeek, categoriaId = categoryId),
        )
    }

    private companion object {
        const val DEFAULT_ROUTINE_NAME = "Mi rutina"
    }
}
