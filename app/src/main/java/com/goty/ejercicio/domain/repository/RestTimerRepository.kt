package com.goty.ejercicio.domain.repository

import com.goty.ejercicio.domain.model.RestTimer
import kotlinx.coroutines.flow.StateFlow

/** Descanso entre series. Se guarda para que siga corriendo aunque se cierre la app. */
interface RestTimerRepository {

    /** Descanso guardado (null si no hay). Puede haber terminado ya: comparar con la hora actual. */
    val restTimer: StateFlow<RestTimer?>

    /** Empieza un descanso nuevo (reemplaza al anterior) y programa el aviso de fin. */
    suspend fun start(totalSeconds: Int)

    /**
     * Da por terminado el descanso que acababa en [endAtMillis]. Si mientras tanto empezó
     * otro descanso, no hace nada y devuelve false.
     */
    suspend fun finishIfCurrent(endAtMillis: Long): Boolean
}
