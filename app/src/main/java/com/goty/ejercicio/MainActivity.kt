package com.goty.ejercicio

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.goty.ejercicio.domain.model.ThemeMode
import com.goty.ejercicio.ui.navigation.AppNavigation
import com.goty.ejercicio.ui.theme.EjercicioTheme
import com.goty.ejercicio.ui.theme.ThemeCrossfade
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    // Mantiene el splash visible un momento para que se vea la animación
    private var keepSplash = true

    override fun onCreate(savedInstanceState: Bundle?) {
        // Debe llamarse antes de super.onCreate()
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // También espera a que se lea el tema guardado, para no mostrar un tema incorrecto
        splashScreen.setKeepOnScreenCondition { keepSplash || viewModel.themeMode.value == null }
        lifecycleScope.launch {
            delay(SPLASH_DURATION_MS)
            keepSplash = false
        }

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM, null -> isSystemInDarkTheme()
            }

            // Fundido con captura: cambia el tema sin recomponer en cada cuadro
            ThemeCrossfade(darkTheme = darkTheme) { appliedDarkTheme ->
                // Ajusta los íconos de la barra de estado/navegación al tema de la app
                DisposableEffect(appliedDarkTheme) {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(
                            Color.TRANSPARENT,
                            Color.TRANSPARENT,
                        ) { appliedDarkTheme },
                        navigationBarStyle = SystemBarStyle.auto(
                            LIGHT_SCRIM,
                            DARK_SCRIM,
                        ) { appliedDarkTheme },
                    )
                    onDispose {}
                }

                EjercicioTheme(darkTheme = appliedDarkTheme) {
                    AppNavigation()
                }
            }
        }
    }

    private companion object {
        // 3 repeticiones de 1 s + una pausa breve para lucir los músculos
        const val SPLASH_DURATION_MS = 3400L

        // Mismos valores que usa enableEdgeToEdge() por defecto
        val LIGHT_SCRIM = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
        val DARK_SCRIM = Color.argb(0x80, 0x1b, 0x1b, 0x1b)
    }
}
