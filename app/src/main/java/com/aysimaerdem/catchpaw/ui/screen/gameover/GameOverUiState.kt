package com.aysimaerdem.catchpaw.ui.screen.gameover

data class GameOverUiState(
    val score: Int = 0,
    val bestScore: Int = 0,
    val missedCount: Int = 0,
    val maxCombo: Int = 0,
    val isNewBest: Boolean = false
)
