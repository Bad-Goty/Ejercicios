package com.goty.ejercicio.domain.model

import java.util.Calendar

/**
 * Día de hoy en formato 1 = lunes … 7 = domingo (como `rutina_dia.diaSemana`).
 * Se usa Calendar porque java.time necesita API 26 (minSdk es 24).
 */
fun todayDayOfWeek(): Int {
    // Calendar: domingo = 1 … sábado = 7
    val calendarDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    return (calendarDay + 5) % 7 + 1
}
