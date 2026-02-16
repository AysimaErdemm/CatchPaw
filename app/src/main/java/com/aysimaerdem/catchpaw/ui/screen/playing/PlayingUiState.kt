package com.aysimaerdem.catchpaw.ui.screen.playing

import com.aysimaerdem.catchpaw.domain.model.Bomb
import com.aysimaerdem.catchpaw.domain.model.ExplosionEffect
import com.aysimaerdem.catchpaw.domain.model.Mouse
import com.aysimaerdem.catchpaw.domain.model.PawEffect

data class PlayingUiState(
    val score: Int = 0,
    val combo: Int = 0,
    val timeLeftMs: Long = 0L,
    val mice: List<Mouse> = emptyList(),
    val pawEffects: List<PawEffect> = emptyList(),
    val bombs: List<Bomb> = emptyList(),
    val explosionEffects: List<ExplosionEffect> = emptyList(),
    val isPaused: Boolean = false
)
