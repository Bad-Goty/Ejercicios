package com.goty.ejercicio.data.timer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.goty.ejercicio.MainActivity
import com.goty.ejercicio.R
import com.goty.ejercicio.domain.model.RestTimer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Notificaciones del descanso:
 * - Cuenta regresiva (naranja) mientras se está fuera de la app.
 * - "Descanso terminado", que vibra (insistente) hasta que se toca o pasa un minuto.
 */
@Singleton
class RestTimerNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val manager = NotificationManagerCompat.from(context)

    /**
     * Notificación con el tiempo que falta, en un recuadro naranja (vista personalizada).
     * El sistema actualiza el contador solo, sin que la app tenga que estar abierta.
     */
    @SuppressLint("MissingPermission") // se revisa en canPostNotifications()
    fun showCountdown(timer: RestTimer) {
        val remaining = timer.endAtMillis - System.currentTimeMillis()
        if (remaining <= 0 || !canPostNotifications()) return
        createChannels()

        val notification = NotificationCompat.Builder(context, CHANNEL_COUNTDOWN)
            .setSmallIcon(R.drawable.ic_stat_rest_timer)
            .setColor(ORANGE)
            // Texto de respaldo (lector de pantalla y versiones que no muestran la vista)
            .setContentTitle("Descanso")
            .setContentText("Prepárate para la siguiente serie")
            // Contenido naranja con la cuenta regresiva; el encabezado lo pone el sistema
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(countdownViews(R.layout.notification_rest_timer_small, remaining))
            .setCustomBigContentView(countdownViews(R.layout.notification_rest_timer_big, remaining))
            .setShowWhen(false) // el tiempo ya se ve grande en el recuadro
            .setOngoing(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
            .setTimeoutAfter(remaining) // desaparece sola al terminar
            .setContentIntent(openAppIntent())
            .build()
        manager.notify(ID_COUNTDOWN, notification)
    }

    /** Aviso de fin de descanso: vibra repetidamente hasta que el usuario lo atiende. */
    @SuppressLint("MissingPermission") // se revisa en canPostNotifications()
    fun showFinished() {
        if (!canPostNotifications()) return
        createChannels()

        val notification = NotificationCompat.Builder(context, CHANNEL_FINISHED)
            .setSmallIcon(R.drawable.ic_stat_rest_timer)
            .setColor(ORANGE)
            .setContentTitle("¡Descanso terminado!")
            .setContentText("Es hora de la siguiente serie")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVibrate(VIBRATION_PATTERN) // para Android 7 (en 8+ lo define el canal)
            .setAutoCancel(true)
            .setTimeoutAfter(FINISHED_TIMEOUT_MS) // deja de vibrar a más tardar en 1 min
            .setContentIntent(openAppIntent())
            .build()
            .apply { flags = flags or Notification.FLAG_INSISTENT } // repite la vibración
        manager.notify(ID_FINISHED, notification)
    }

    /** Vista de la notificación con un cronómetro que cuenta hacia atrás [remainingMillis]. */
    private fun countdownViews(layout: Int, remainingMillis: Long): RemoteViews =
        RemoteViews(context.packageName, layout).apply {
            // El cronómetro usa el reloj desde el arranque: termina en "ahora + lo que falta"
            setChronometer(R.id.rest_chronometer, SystemClock.elapsedRealtime() + remainingMillis, null, true)
            setChronometerCountDown(R.id.rest_chronometer, true)
        }

    fun cancelAll() {
        manager.cancel(ID_COUNTDOWN)
        manager.cancel(ID_FINISHED)
    }

    fun cancelCountdown() {
        manager.cancel(ID_COUNTDOWN)
    }

    private fun canPostNotifications(): Boolean =
        manager.areNotificationsEnabled() &&
            (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED)

    private fun openAppIntent(): PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    private fun createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val system = context.getSystemService(NotificationManager::class.java)
        system.createNotificationChannel(
            NotificationChannel(CHANNEL_COUNTDOWN, "Descanso en curso", NotificationManager.IMPORTANCE_LOW).apply {
                description = "Muestra el tiempo que falta del descanso entre series"
                setShowBadge(false)
            }
        )
        system.createNotificationChannel(
            NotificationChannel(CHANNEL_FINISHED, "Fin del descanso", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Vibra cuando termina el descanso entre series"
                enableVibration(true)
                vibrationPattern = VIBRATION_PATTERN
                setSound(null, null) // solo vibración
            }
        )
    }

    private companion object {
        const val CHANNEL_COUNTDOWN = "rest_timer_countdown"
        const val CHANNEL_FINISHED = "rest_timer_finished"
        const val ID_COUNTDOWN = 2001
        const val ID_FINISHED = 2002
        const val FINISHED_TIMEOUT_MS = 60_000L

        /** Naranja de la marca (BrandOrange #FF6D00). */
        const val ORANGE = 0xFFFF6D00.toInt()

        /** Espera, vibra, pausa, vibra... (ms). */
        val VIBRATION_PATTERN = longArrayOf(0, 600, 300, 600, 300, 600)
    }
}
