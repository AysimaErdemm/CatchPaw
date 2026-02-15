package com.catchpaw.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameScoreDao {

    @Insert
    suspend fun insertScore(entity: GameScoreEntity)

    @Query("SELECT MAX(score) FROM game_scores")
    fun getBestScore(): Flow<Int?>

    @Query("SELECT * FROM game_scores ORDER BY timestamp DESC")
    fun getAllScores(): Flow<List<GameScoreEntity>>
}
