package com.aysimaerdem.catchpaw.ui.screen.playing

import com.aysimaerdem.catchpaw.domain.model.ActivePowerUp
import com.aysimaerdem.catchpaw.domain.model.GameConfig
import com.aysimaerdem.catchpaw.domain.model.Bomb
import com.aysimaerdem.catchpaw.domain.model.ExplosionEffect
import com.aysimaerdem.catchpaw.domain.model.Mouse
import com.aysimaerdem.catchpaw.domain.model.PawEffect
import com.aysimaerdem.catchpaw.domain.model.PowerUp

data class PlayingUiState(
    val score: Int = 0,
    val combo: Int = 0,
    val timeLeftMs: Long = 0L,
    val totalDurationMs: Long = GameConfig.GAME_DURATION_MS,
    val mice: List<Mouse> = emptyList(),
    val pawEffects: List<PawEffect> = emptyList(),
    val bombs: List<Bomb> = emptyList(),
    val explosionEffects: List<ExplosionEffect> = emptyList(),
    val powerUps: List<PowerUp> = emptyList(),
    val activePowerUps: List<ActivePowerUp> = emptyList(),
    val isPaused: Boolean = false
)
