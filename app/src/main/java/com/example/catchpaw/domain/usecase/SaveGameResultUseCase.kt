package com.catchpaw.domain.usecase

import com.catchpaw.domain.repository.ScoreRepository
import javax.inject.Inject

class SaveGameResultUseCase @Inject constructor(
    private val repository: ScoreRepository
) {
    suspend operator fun invoke(score: Int, missedCount: Int) {
        repository.saveGameResult(score, missedCount)
    }
}
