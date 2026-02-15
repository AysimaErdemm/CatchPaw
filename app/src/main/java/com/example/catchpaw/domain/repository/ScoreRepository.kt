package com.catchpaw.domain.repository

import kotlinx.coroutines.flow.Flow

interface ScoreRepository {
    fun getBestScore(): Flow<Int>
    suspend fun saveGameResult(score: Int, missedCount: Int)
}
