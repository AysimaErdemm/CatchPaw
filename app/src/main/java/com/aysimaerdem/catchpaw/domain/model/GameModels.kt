package com.aysimaerdem.catchpaw.domain.model

data class Mouse(
    val id: Int,
    val x: Float,
    val y: Float,
    val emoji: String = "🐁",
    val isDying: Boolean = false,
    val dyingStartTime: Long = 0L
)

data class PawEffect(
    val id: Int,
    val x: Float,
    val y: Float
)

data class GrassLine(
    val x: Float,
    val heightRatio: Float,
    val offsetX: Float,
    val isDark: Boolean
)

data class GameResult(
    val score: Int,
    val bestScore: Int,
    val missedCount: Int,
    val maxCombo: Int,
    val isNewBest: Boolean
)

data class Bomb(
    val id: Int,
    val x: Float,
    val y: Float,
    val isDying: Boolean = false,
    val dyingStartTime: Long = 0L
)

data class ExplosionEffect(
    val id: Int,
    val x: Float,
    val y: Float
)

object GameConfig {
    const val GAME_DURATION_MS = 60_000L
    const val MOUSE_FADE_OUT_MS = 700L
    const val COMBO_WINDOW_MS = 3_000L
    const val MOUSE_SIZE = 60f
    const val TOP_BAR_HEIGHT = 120f

    const val BOMB_PENALTY_MS = 5_000L
    const val BOMB_SPAWN_CHANCE = 0.25f
    const val BOMB_LIFETIME_MS = 2_500L

    // Difficulty progression — values lerp from EASY → HARD as score reaches DIFFICULTY_SCORE_CAP
    const val DIFFICULTY_SCORE_CAP = 30

    const val EASY_MOUSE_LIFETIME_MS = 3_000L
    const val HARD_MOUSE_LIFETIME_MS = 1_000L

    const val EASY_SPAWN_INTERVAL_MS = 1_500L
    const val HARD_SPAWN_INTERVAL_MS = 400L

    const val EASY_MAX_MICE = 1
    const val HARD_MAX_MICE = 5
}
