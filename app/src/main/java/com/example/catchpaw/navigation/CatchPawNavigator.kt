package com.catchpaw.navigation

import androidx.navigation.NavHostController

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

    fun navigateToGameOver(score: Int, bestScore: Int, missedCount: Int, maxCombo: Int) {
        navController.navigate(Screen.GameOver.createRoute(score, bestScore, missedCount, maxCombo)) {
            launchSingleTop = true
            popUpTo(Screen.Start.route) { inclusive = true }
        }
    }

    fun popBackStack(): Boolean = navController.popBackStack()
}
