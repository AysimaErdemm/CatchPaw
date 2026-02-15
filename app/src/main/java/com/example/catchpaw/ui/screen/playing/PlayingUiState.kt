package com.example.catchpaw.ui.screen.playing

import com.example.catchpaw.domain.model.Mouse
import com.example.catchpaw.domain.model.PawEffect

data class PlayingUiState(
    val score: Int = 0,
    val combo: Int = 0,
    val timeLeftMs: Long = 0L,
    val mice: List<Mouse> = emptyList(),
    val pawEffects: List<PawEffect> = emptyList(),
    val isPaused: Boolean = false
)
