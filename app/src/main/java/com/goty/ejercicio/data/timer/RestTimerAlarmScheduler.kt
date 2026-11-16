package com.goty.ejercicio.data.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Programa con AlarmManager el aviso de fin de descanso. La alarma despierta a
 * [RestTimerReceiver] aunque la app esté cerrada.
 *
 * Usa alarma exacta si el usuario dio el permiso "Alarmas y recordatorios"; si no, una
 * alarma normal (Android puede retrasarla un poco).
 */
@Singleton
class RestTimerAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun canScheduleExactAlarms(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    fun schedule(endAtMillis: Long) {
        val pendingIntent = pendingIntent(endAtMillis)
        if (canScheduleExactAlarms()) {
            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endAtMillis, pendingIntent)
                return
            } catch (_: SecurityException) {
                // El permiso se quitó justo ahora: se usa la alarma normal
            }
        }
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endAtMillis, pendingIntent)
    }

    /** Mismo PendingIntent siempre: programar uno nuevo reemplaza al anterior. */
    private fun pendingIntent(endAtMillis: Long): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, RestTimerReceiver::class.java).putExtra(EXTRA_END_AT, endAtMillis),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    companion object {
        const val EXTRA_END_AT = "rest_end_at"
        private const val REQUEST_CODE = 1001
    }
}
