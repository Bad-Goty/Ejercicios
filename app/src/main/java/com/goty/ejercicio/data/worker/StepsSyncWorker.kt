package com.goty.ejercicio.data.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.goty.ejercicio.domain.repository.StepsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

/**
 * Trabajo en segundo plano: lee el sensor de pasos y guarda el conteo del día,
 * para que los pasos se registren aunque la app no se abra.
 */
@HiltWorker
class StepsSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val stepsRepository: StepsRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Sin permiso o sin sensor no hay nada que hacer; se intentará en la siguiente ejecución.
        if (!hasActivityRecognitionPermission() || !stepsRepository.isStepSensorAvailable) {
            return Result.success()
        }
        stepsRepository.syncStepsFromSensor()
        return Result.success()
    }

    private fun hasActivityRecognitionPermission(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACTIVITY_RECOGNITION,
            ) == PackageManager.PERMISSION_GRANTED

    companion object {
        private const val UNIQUE_WORK_NAME = "steps_sync"

        /** Intervalo mínimo que permite Android para trabajos periódicos. */
        private const val REPEAT_INTERVAL_MINUTES = 15L

        /**
         * Programa la sincronización periódica. Si ya estaba programada, la conserva
         * (no la reinicia), así que es seguro llamarla en cada inicio de la app.
         */
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<StepsSyncWorker>(
                REPEAT_INTERVAL_MINUTES, TimeUnit.MINUTES,
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }
    }
}
