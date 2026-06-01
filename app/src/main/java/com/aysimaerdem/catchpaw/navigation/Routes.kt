package com.aysimaerdem.catchpaw.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.aysimaerdem.catchpaw.domain.model.ChallengeMode

sealed class Screen(val route: String) {
    data object Start : Screen("start")
    data object Playing : Screen("playing/{mode}") {
        fun createRoute(mode: ChallengeMode = ChallengeMode.NORMAL): String = "playing/${mode.ordinal}"
        val arguments = listOf(navArgument("mode") {
            type = NavType.IntType
            defaultValue = ChallengeMode.NORMAL.ordinal
        })
    }
    data object GameOver : Screen("game_over/{score}/{bestScore}/{missedCount}/{maxCombo}") {
        fun createRoute(score: Int, bestScore: Int, missedCount: Int, maxCombo: Int): String =
            "game_over/$score/$bestScore/$missedCount/$maxCombo"
    }
}
