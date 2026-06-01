package com.aysimaerdem.catchpaw.domain.model

enum class MouseType(val emoji: String, val basePoints: Int, val lifetimeMultiplier: Float, val spawnWeight: Int) {
    NORMAL("🐁", 1, 1.0f, 50),
    SLOW("🐁", 2, 1.6f, 20),
    FAST("🐁", 1, 0.5f, 20),
    BONUS("🐁", 5, 0.7f, 10)
}

data class Mouse(
    val id: Int,
    val x: Float,
    val y: Float,
    val type: MouseType = MouseType.NORMAL,
    val spawnTimeMs: Long = 0L,
    val lifetimeMs: Long = 3000L,
    val isDying: Boolean = false,
    val dyingStartTime: Long = 0L
) {
    val emoji: String get() = type.emoji
}

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
    val spawnTimeMs: Long = 0L,
    val isDying: Boolean = false,
    val dyingStartTime: Long = 0L
)

data class ExplosionEffect(
    val id: Int,
    val x: Float,
    val y: Float
)

enum class PowerUpType(val emoji: String, val durationMs: Long) {
    TIME_FREEZE("❄️", 5_000L),
    DOUBLE_POINTS("⚡", 8_000L),
    SHIELD("🛡️", 10_000L)
}

data class PowerUp(
    val id: Int,
    val type: PowerUpType,
    val x: Float,
    val y: Float,
    val spawnTimeMs: Long = 0L,
    val isDying: Boolean = false,
    val dyingStartTime: Long = 0L
)

data class ActivePowerUp(
    val type: PowerUpType,
    val endsAtMs: Long
)

enum class ChallengeMode { NORMAL, DAILY }

object GameConfig {
    const val MOUSE_FADE_OUT_MS = 700L
    const val COMBO_WINDOW_MS = 3_000L
    const val MOUSE_SIZE = 60f
    const val TOP_BAR_HEIGHT = 120f

    const val BOMB_PENALTY_MS = 5_000L
    const val BOMB_SPAWN_CHANCE = 0.25f
    const val BOMB_LIFETIME_MS = 2_500L

    const val POWER_UP_SPAWN_CHANCE = 0.12f
    const val POWER_UP_LIFETIME_MS = 6_000L

    const val DIFFICULTY_SCORE_CAP = 30

    const val EASY_MOUSE_LIFETIME_MS = 3_000L
    const val HARD_MOUSE_LIFETIME_MS = 1_000L

    const val EASY_SPAWN_INTERVAL_MS = 1_500L
    const val HARD_SPAWN_INTERVAL_MS = 400L

    const val EASY_MAX_MICE = 1
    const val HARD_MAX_MICE = 5

    // Modes
    const val GAME_DURATION_MS = 60_000L
    const val DAILY_DURATION_MS = 90_000L
    const val DAILY_DIFFICULTY_CAP = 20
}
