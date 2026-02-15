package com.catchpaw.domain.usecase

import com.catchpaw.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBestScoreUseCase @Inject constructor(
    private val repository: ScoreRepository
) {
    operator fun invoke(): Flow<Int> = repository.getBestScore()
}
