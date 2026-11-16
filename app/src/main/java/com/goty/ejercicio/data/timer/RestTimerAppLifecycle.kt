package com.goty.ejercicio.data.timer

import com.goty.ejercicio.domain.repository.RestTimerRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sabe si la app está en pantalla y muestra/oculta la notificación del descanso:
 * - Al salir de la app: si hay descanso, aparece la cuenta regresiva.
 * - Al volver: se quitan las notificaciones (el descanso se ve dentro de la app).
 * MainActivity le avisa en onStart/onStop.
 */
@Singleton
class RestTimerAppLifecycle @Inject constructor(
    private val repository: RestTimerRepository,
    private val notifier: RestTimerNotifier,
) {
    @Volatile
    var isAppInForeground: Boolean = false
        private set

    fun onAppForeground() {
        isAppInForeground = true
        notifier.cancelAll()
    }

    fun onAppBackground() {
        isAppInForeground = false
        val timer = repository.restTimer.value ?: return
        if (timer.endAtMillis > System.currentTimeMillis()) notifier.showCountdown(timer)
    }
}
