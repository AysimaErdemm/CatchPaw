package com.example.catchpaw.domain.engine

import com.example.catchpaw.domain.model.Bomb
import com.example.catchpaw.domain.model.GameConfig
import com.example.catchpaw.domain.model.GameResult
import com.example.catchpaw.domain.model.Mouse
import kotlin.random.Random

class GameEngine {

    var score: Int = 0
        private set
    var missedCount: Int = 0
        private set
    var combo: Int = 0
        private set
    var maxCombo: Int = 0
        private set
    var lastCatchTime: Long = 0L
        private set

    private var mouseIdCounter: Int = 0
    private var bombIdCounter: Int = 0

    /** 0 → 1 progress based on current score */
    fun difficultyProgress(): Float =
        (score.toFloat() / GameConfig.DIFFICULTY_SCORE_CAP).coerceIn(0f, 1f)

    fun onMouseCaught(currentTimeMs: Long): Int {
        combo = if (currentTimeMs - lastCatchTime < GameConfig.COMBO_WINDOW_MS) combo + 1 else 1
        if (combo > maxCombo) maxCombo = combo
        lastCatchTime = currentTimeMs
        val points = if (combo > 1) combo else 1
        score += points
        return points
    }

    fun onMouseMissed() {
        combo = 0
        missedCount++
    }

    fun createMouse(containerWidth: Float, containerHeight: Float, topBarHeightPx: Float = GameConfig.TOP_BAR_HEIGHT): Mouse {
        val mouseSize = GameConfig.MOUSE_SIZE
        return Mouse(
            id = mouseIdCounter++,
            x = Random.nextFloat() * (containerWidth - mouseSize * 2) + mouseSize / 2,
            y = topBarHeightPx + Random.nextFloat() * (containerHeight - topBarHeightPx - mouseSize * 2)
        )
    }

    /** How long a mouse stays alive before it starts dying — gets shorter as score increases */
    fun calculateMouseLifetime(): Long {
        val p = difficultyProgress()
        return lerp(GameConfig.EASY_MOUSE_LIFETIME_MS, GameConfig.HARD_MOUSE_LIFETIME_MS, p)
    }

    /** Spawn delay between mice — gets shorter as score increases */
    fun calculateSpawnInterval(): Long {
        val p = difficultyProgress()
        return lerp(GameConfig.EASY_SPAWN_INTERVAL_MS, GameConfig.HARD_SPAWN_INTERVAL_MS, p)
    }

    /** Max simultaneous mice on screen — increases as score increases */
    fun calculateMaxMice(): Int {
        val p = difficultyProgress()
        return (GameConfig.EASY_MAX_MICE + p * (GameConfig.HARD_MAX_MICE - GameConfig.EASY_MAX_MICE)).toInt()
    }

    fun createBomb(containerWidth: Float, containerHeight: Float, topBarHeightPx: Float = GameConfig.TOP_BAR_HEIGHT): Bomb {
        val size = GameConfig.MOUSE_SIZE
        return Bomb(
            id = bombIdCounter++,
            x = Random.nextFloat() * (containerWidth - size * 2) + size / 2,
            y = topBarHeightPx + Random.nextFloat() * (containerHeight - topBarHeightPx - size * 2)
        )
    }

    fun onBombClicked() {
        combo = 0
    }

    fun shouldSpawnBomb(): Boolean = Random.nextFloat() < GameConfig.BOMB_SPAWN_CHANCE

    fun buildGameResult(bestScore: Int): GameResult {
        val newBest = score > bestScore
        val finalBest = if (newBest) score else bestScore
        return GameResult(
            score = score,
            bestScore = finalBest,
            missedCount = missedCount,
            maxCombo = maxCombo,
            isNewBest = newBest
        )
    }

    fun reset() {
        score = 0
        missedCount = 0
        combo = 0
        maxCombo = 0
        lastCatchTime = 0L
        mouseIdCounter = 0
        bombIdCounter = 0
    }

    private fun lerp(start: Long, end: Long, fraction: Float): Long =
        (start + fraction * (end - start)).toLong()
}
