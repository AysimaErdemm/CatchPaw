package com.example.catchpaw.navigation

sealed class Screen(val route: String) {
    data object Start : Screen("start")
    data object Playing : Screen("playing")
    data object GameOver : Screen("game_over/{score}/{bestScore}/{missedCount}/{maxCombo}") {
        fun createRoute(score: Int, bestScore: Int, missedCount: Int, maxCombo: Int): String =
            "game_over/$score/$bestScore/$missedCount/$maxCombo"
    }
}
