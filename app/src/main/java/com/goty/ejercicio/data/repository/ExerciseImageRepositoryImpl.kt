package com.goty.ejercicio.data.repository

import android.content.Context
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.goty.ejercicio.domain.repository.ExerciseImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

/**
 * Copia la imagen elegida a `files/exercise_media/` dentro de la app.
 * Se copia porque el permiso que da el selector de fotos sobre la URI es temporal:
 * si solo guardáramos la URI, la imagen dejaría de abrirse más adelante.
 */
class ExerciseImageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ExerciseImageRepository {

    private val mediaDir: File
        get() = File(context.filesDir, "exercise_media").apply { mkdirs() }

    override suspend fun isWithinSize(uri: String, maxBytes: Long): Boolean =
        withContext(Dispatchers.IO) {
            val size = context.contentResolver
                .query(uri.toUri(), arrayOf(OpenableColumns.SIZE), null, null, null)
                ?.use { cursor ->
                    if (cursor.moveToFirst() && !cursor.isNull(0)) cursor.getLong(0) else null
                }
            size == null || size <= maxBytes // si el sistema no informa el tamaño, se permite
        }

    override suspend fun importImage(uri: String): String = withContext(Dispatchers.IO) {
        val source = uri.toUri()
        val extension = when (context.contentResolver.getType(source)) {
            "image/gif" -> "gif"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "jpg"
        }
        val target = File(mediaDir, "${UUID.randomUUID()}.$extension")
        val input = context.contentResolver.openInputStream(source)
            ?: throw IOException("No se pudo abrir la imagen")
        input.use { inStream -> target.outputStream().use { inStream.copyTo(it) } }
        target.absolutePath
    }

    override suspend fun deleteImage(path: String) {
        withContext(Dispatchers.IO) { File(path).delete() }
    }
}
