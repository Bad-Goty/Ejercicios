package com.goty.ejercicio.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.goty.ejercicio.data.local.dao.EjercicioDao
import com.goty.ejercicio.data.repository.ExerciseRepositoryImpl
import com.goty.ejercicio.domain.repository.ExerciseImageRepository
import com.goty.ejercicio.domain.usecase.SaveExerciseUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Verifica que "Guardar ejercicio" guarda el nombre y el músculo principal en Room. */
@RunWith(AndroidJUnit4::class)
class SaveExerciseTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: EjercicioDao
    private lateinit var saveExercise: SaveExerciseUseCase

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // BD en memoria: no toca los datos reales de la app. Con la misma precarga.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .addCallback(PrepopulateCallback())
            .allowMainThreadQueries()
            .build()
        dao = db.ejercicioDao()
        saveExercise = SaveExerciseUseCase(ExerciseRepositoryImpl(dao), FakeImageRepository())
    }

    /** Simula la copia de la imagen sin tocar archivos reales. */
    private class FakeImageRepository : ExerciseImageRepository {
        override suspend fun isWithinSize(uri: String, maxBytes: Long) = true
        override suspend fun importImage(uri: String) = "/fake/exercise_media/foto.gif"
        override suspend fun deleteImage(path: String) = Unit
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun guardaEjercicioConMusculoPrincipal() = runBlocking {
        val id = saveExercise("  Curl de bíceps  ", mainMuscleId = 21) // 21 = Bíceps braquial

        val guardado = dao.obtenerConMusculos(id).first()!!
        assertEquals("Curl de bíceps", guardado.ejercicio.nombre) // sin espacios sobrantes
        assertEquals(listOf("Bíceps braquial"), guardado.musculos.map { it.nombre })

        val esPrincipal = db.openHelper.readableDatabase
            .query("SELECT esPrincipal FROM ejercicio_musculo WHERE ejercicioId = $id AND musculoId = 21")
            .use { c -> c.moveToFirst() && c.getInt(0) == 1 }
        assertTrue("El músculo debe quedar como principal", esPrincipal)
    }

    @Test
    fun guardaEjercicioSinMusculo() = runBlocking {
        val id = saveExercise("Plancha", mainMuscleId = null)

        val guardado = dao.obtenerConMusculos(id).first()!!
        assertEquals("Plancha", guardado.ejercicio.nombre)
        assertTrue(guardado.musculos.isEmpty())
    }

    @Test
    fun guardaLaRutaDeLaImagen() = runBlocking {
        val id = saveExercise("Sentadilla", mainMuscleId = 29, imageUri = "content://media/foto.gif")

        val guardado = dao.obtenerConMusculos(id).first()!!
        assertEquals("/fake/exercise_media/foto.gif", guardado.ejercicio.foto)
    }

    @Test(expected = IllegalArgumentException::class)
    fun noGuardaSinNombre() {
        runBlocking { saveExercise("   ", mainMuscleId = 21) }
    }
}
