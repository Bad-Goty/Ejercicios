package com.goty.ejercicio.data.repository

import com.goty.ejercicio.data.preferences.RestTimerDataSource
import com.goty.ejercicio.data.timer.RestTimerAlarmScheduler
import com.goty.ejercicio.domain.model.RestTimer
import com.goty.ejercicio.domain.repository.RestTimerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestTimerRepositoryImpl @Inject constructor(
    private val dataSource: RestTimerDataSource,
    private val scheduler: RestTimerAlarmScheduler,
) : RestTimerRepository {

    // Vive mientras viva la app, para tener siempre a mano el último valor guardado
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val restTimer: StateFlow<RestTimer?> =
        dataSource.restTimer.stateIn(scope, SharingStarted.Eagerly, null)

    override suspend fun start(totalSeconds: Int) {
        val timer = RestTimer(
            endAtMillis = System.currentTimeMillis() + totalSeconds * 1_000L,
            totalSeconds = totalSeconds,
        )
        dataSource.save(timer)
        scheduler.schedule(timer.endAtMillis)
    }

    override suspend fun finishIfCurrent(endAtMillis: Long): Boolean =
        dataSource.clearIfEndsAt(endAtMillis)
}
