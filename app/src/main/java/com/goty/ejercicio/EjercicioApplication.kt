package com.goty.ejercicio

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.goty.ejercicio.data.worker.StepsSyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EjercicioApplication : Application(), Configuration.Provider {

    /** Permite que Hilt inyecte dependencias en los Workers (@HiltWorker). */
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Registra los pasos en segundo plano cada 15 min, aunque no se abra la app
        StepsSyncWorker.schedule(this)
    }
}
