package com.aysimaerdem.catchpaw.ui.screen.start

data class StartUiState(
    val bestScore: Int = 0,
    val isLoading: Boolean = true,
    val dailyBestScore: Int = 0,
    val isDailyCompleted: Boolean = false,
    val weeklyTotalScore: Int = 0,
    val daysPlayedThisWeek: Int = 0
)
