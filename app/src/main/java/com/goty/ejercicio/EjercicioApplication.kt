package com.goty.ejercicio

import android.app.Application
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import com.goty.ejercicio.data.worker.StepsSyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EjercicioApplication : Application(), Configuration.Provider, SingletonImageLoader.Factory {

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

    /** Cargador de imágenes de Coil para toda la app, con soporte para GIF animados. */
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(AnimatedImageDecoder.Factory()) // decodificador nativo (Android 9+)
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
}
