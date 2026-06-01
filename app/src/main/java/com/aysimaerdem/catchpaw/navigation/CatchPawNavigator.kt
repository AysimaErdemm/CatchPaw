package com.aysimaerdem.catchpaw.navigation

import androidx.navigation.NavHostController
import com.aysimaerdem.catchpaw.domain.model.ChallengeMode

class CatchPawNavigator(
    private val navController: NavHostController
) {
    val currentRoute: String?
        get() = navController.currentDestination?.route

    fun navigateTo(screen: Screen) {
        navController.navigate(screen.route) {
            launchSingleTop = true
            when (screen) {
                is Screen.Start -> popUpTo(0) { inclusive = true }
                is Screen.Playing -> popUpTo(Screen.Start.route) { inclusive = true }
                is Screen.GameOver -> popUpTo(Screen.Start.route) { inclusive = true }
            }
        }
    }

    fun navigateToPlaying(mode: ChallengeMode = ChallengeMode.NORMAL) {
        navController.navigate(Screen.Playing.createRoute(mode)) {
            launchSingleTop = true
            popUpTo(Screen.Start.route) { inclusive = true }
        }
    }

    fun navigateToGameOver(score: Int, bestScore: Int, missedCount: Int, maxCombo: Int) {
        navController.navigate(Screen.GameOver.createRoute(score, bestScore, missedCount, maxCombo)) {
            launchSingleTop = true
            popUpTo(Screen.Start.route) { inclusive = true }
        }
    }

    fun popBackStack(): Boolean = navController.popBackStack()
}
