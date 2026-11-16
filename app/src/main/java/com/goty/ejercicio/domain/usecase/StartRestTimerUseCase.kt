package com.goty.ejercicio.domain.usecase

import com.goty.ejercicio.domain.repository.RestTimerRepository
import javax.inject.Inject

/** Empieza el descanso entre series. */
class StartRestTimerUseCase @Inject constructor(
    private val repository: RestTimerRepository,
) {
    suspend operator fun invoke(totalSeconds: Int) = repository.start(totalSeconds)
}
