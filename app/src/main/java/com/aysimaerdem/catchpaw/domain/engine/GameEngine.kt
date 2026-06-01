package com.aysimaerdem.catchpaw.domain.engine

import com.aysimaerdem.catchpaw.domain.model.*
import kotlin.random.Random

class GameEngine(
    private val random: Random = Random,
    private val difficultyCap: Int = GameConfig.DIFFICULTY_SCORE_CAP
) {

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
    private var powerUpIdCounter: Int = 0

    fun difficultyProgress(): Float =
        (score.toFloat() / difficultyCap).coerceIn(0f, 1f)

    fun onMouseCaught(currentTimeMs: Long, basePoints: Int = 1): Int {
        combo = if (currentTimeMs - lastCatchTime < GameConfig.COMBO_WINDOW_MS) combo + 1 else 1
        if (combo > maxCombo) maxCombo = combo
        lastCatchTime = currentTimeMs
        val points = if (combo > 1) combo * basePoints else basePoints
        score += points
        return points
    }

    fun onMouseMissed() {
        combo = 0
        missedCount++
    }

    fun createMouse(containerWidth: Float, containerHeight: Float, topBarHeightPx: Float = GameConfig.TOP_BAR_HEIGHT): Mouse {
        val mouseSize = GameConfig.MOUSE_SIZE
        val type = pickMouseType()
        val lifetime = calculateMouseLifetime(type)
        return Mouse(
            id = mouseIdCounter++,
            type = type,
            x = random.nextFloat() * (containerWidth - mouseSize * 2) + mouseSize / 2,
            y = topBarHeightPx + random.nextFloat() * (containerHeight - topBarHeightPx - mouseSize * 2),
            spawnTimeMs = System.currentTimeMillis(),
            lifetimeMs = lifetime
        )
    }

    private fun pickMouseType(): MouseType {
        val totalWeight = MouseType.entries.sumOf { it.spawnWeight }
        var r = random.nextInt(totalWeight)
        for (type in MouseType.entries) {
            r -= type.spawnWeight
            if (r < 0) return type
        }
        return MouseType.NORMAL
    }

    fun calculateMouseLifetime(type: MouseType = MouseType.NORMAL): Long {
        val p = difficultyProgress()
        val base = lerp(GameConfig.EASY_MOUSE_LIFETIME_MS, GameConfig.HARD_MOUSE_LIFETIME_MS, p)
        return (base * type.lifetimeMultiplier).toLong()
    }

    fun calculateSpawnInterval(): Long {
        val p = difficultyProgress()
        return lerp(GameConfig.EASY_SPAWN_INTERVAL_MS, GameConfig.HARD_SPAWN_INTERVAL_MS, p)
    }

    fun calculateMaxMice(): Int {
        val p = difficultyProgress()
        return (GameConfig.EASY_MAX_MICE + p * (GameConfig.HARD_MAX_MICE - GameConfig.EASY_MAX_MICE)).toInt()
    }

    fun createBomb(containerWidth: Float, containerHeight: Float, topBarHeightPx: Float = GameConfig.TOP_BAR_HEIGHT): Bomb {
        val size = GameConfig.MOUSE_SIZE
        return Bomb(
            id = bombIdCounter++,
            x = random.nextFloat() * (containerWidth - size * 2) + size / 2,
            y = topBarHeightPx + random.nextFloat() * (containerHeight - topBarHeightPx - size * 2),
            spawnTimeMs = System.currentTimeMillis()
        )
    }

    fun onBombClicked() {
        combo = 0
    }

    fun shouldSpawnBomb(): Boolean = random.nextFloat() < GameConfig.BOMB_SPAWN_CHANCE

    fun createPowerUp(containerWidth: Float, containerHeight: Float, topBarHeightPx: Float = GameConfig.TOP_BAR_HEIGHT): PowerUp {
        val size = GameConfig.MOUSE_SIZE
        val type = PowerUpType.entries[random.nextInt(PowerUpType.entries.size)]
        return PowerUp(
            id = powerUpIdCounter++,
            type = type,
            x = random.nextFloat() * (containerWidth - size * 2) + size / 2,
            y = topBarHeightPx + random.nextFloat() * (containerHeight - topBarHeightPx - size * 2),
            spawnTimeMs = System.currentTimeMillis()
        )
    }

    fun shouldSpawnPowerUp(): Boolean = random.nextFloat() < GameConfig.POWER_UP_SPAWN_CHANCE

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
        powerUpIdCounter = 0
    }

    private fun lerp(start: Long, end: Long, fraction: Float): Long =
        (start + fraction * (end - start)).toLong()
}
