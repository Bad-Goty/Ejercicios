package com.goty.ejercicio.domain.model

/**
 * Descanso entre series en curso.
 * @param endAtMillis momento en que termina (reloj del sistema, epoch en ms). Se usa el reloj
 *   real y no uno relativo para que siga siendo válido aunque se cierre la app.
 */
data class RestTimer(
    val endAtMillis: Long,
    val totalSeconds: Int,
)
