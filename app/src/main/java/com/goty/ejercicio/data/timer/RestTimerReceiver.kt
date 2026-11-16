package com.goty.ejercicio.data.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.goty.ejercicio.domain.repository.RestTimerRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Lo despierta la alarma cuando termina el descanso (aunque la app esté cerrada).
 * Si el usuario no está dentro de la app, muestra el aviso que vibra.
 */
class RestTimerReceiver : BroadcastReceiver() {

    /** Acceso a las dependencias de Hilt desde el receiver. */
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Dependencies {
        fun restTimerRepository(): RestTimerRepository
        fun restTimerNotifier(): RestTimerNotifier
        fun restTimerAppLifecycle(): RestTimerAppLifecycle
    }

    override fun onReceive(context: Context, intent: Intent) {
        val deps = EntryPointAccessors.fromApplication(context.applicationContext, Dependencies::class.java)
        val endAt = intent.getLongExtra(RestTimerAlarmScheduler.EXTRA_END_AT, 0L)
        val pending = goAsync() // permite terminar el trabajo de DataStore en segundo plano
        scope.launch {
            try {
                // Si ya empezó otro descanso, esta alarma es vieja y se ignora
                if (deps.restTimerRepository().finishIfCurrent(endAt)) {
                    deps.restTimerNotifier().cancelCountdown()
                    if (!deps.restTimerAppLifecycle().isAppInForeground) {
                        deps.restTimerNotifier().showFinished()
                    }
                }
            } finally {
                pending.finish()
            }
        }
    }

    private companion object {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}
