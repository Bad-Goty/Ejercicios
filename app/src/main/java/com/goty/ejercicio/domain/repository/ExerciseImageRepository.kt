package com.goty.ejercicio.domain.repository

/**
 * Manejo de la foto/GIF de un ejercicio. Las imágenes se reciben como URI (String)
 * del selector de fotos y se copian al almacenamiento interno de la app.
 */
interface ExerciseImageRepository {

    /** true si la imagen pesa como máximo [maxBytes]. */
    suspend fun isWithinSize(uri: String, maxBytes: Long): Boolean

    /** Copia la imagen al almacenamiento de la app y devuelve la ruta del archivo copiado. */
    suspend fun importImage(uri: String): String

    /** Borra una imagen copiada antes (por si falla el guardado). */
    suspend fun deleteImage(path: String)
}
