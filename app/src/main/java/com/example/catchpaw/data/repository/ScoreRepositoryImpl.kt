package com.catchpaw.data.repository

import com.catchpaw.data.local.GameScoreDao
import com.catchpaw.data.local.GameScoreEntity
import com.catchpaw.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScoreRepositoryImpl @Inject constructor(
    private val dao: GameScoreDao
) : ScoreRepository {

    override fun getBestScore(): Flow<Int> =
        dao.getBestScore().map { it ?: 0 }

    override suspend fun saveGameResult(score: Int, missedCount: Int) {
        dao.insertScore(
            GameScoreEntity(
                score = score,
                missedCount = missedCount,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
