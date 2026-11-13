package com.goty.ejercicio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.goty.ejercicio.ui.navigation.AppNavigation
import com.goty.ejercicio.ui.theme.EjercicioTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Mantiene el splash visible un momento para que se vea la animación
    private var keepSplash = true

    override fun onCreate(savedInstanceState: Bundle?) {
        // Debe llamarse antes de super.onCreate()
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { keepSplash }
        lifecycleScope.launch {
            delay(SPLASH_DURATION_MS)
            keepSplash = false
        }

        enableEdgeToEdge()
        setContent {
            EjercicioTheme {
                AppNavigation()
            }
        }
    }

    private companion object {
        // 3 repeticiones de 1 s + una pausa breve para lucir los músculos
        const val SPLASH_DURATION_MS = 3400L
    }
}
