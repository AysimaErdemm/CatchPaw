package com.aysimaerdem.catchpaw.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aysimaerdem.catchpaw.domain.model.ChallengeMode
import com.aysimaerdem.catchpaw.ui.screen.gameover.GameOverScreen
import com.aysimaerdem.catchpaw.ui.screen.playing.PlayingScreen
import com.aysimaerdem.catchpaw.ui.screen.start.StartScreen

@Composable
fun CatchPawNavHost(navController: NavHostController) {
    val navigator = remember(navController) { CatchPawNavigator(navController) }

    NavHost(
        navController = navController,
        startDestination = Screen.Start.route,
        enterTransition = {
            fadeIn(tween(400)) + scaleIn(tween(400), initialScale = 0.92f)
        },
        exitTransition = {
            fadeOut(tween(300)) + scaleOut(tween(300), targetScale = 1.05f)
        }
    ) {
        composable(Screen.Start.route) {
            StartScreen(
                onStartGame = { navigator.navigateToPlaying(ChallengeMode.NORMAL) },
                onStartDailyChallenge = { navigator.navigateToPlaying(ChallengeMode.DAILY) }
            )
        }

        composable(
            route = Screen.Playing.route,
            arguments = Screen.Playing.arguments
        ) {
            PlayingScreen(
                onGameOver = { result ->
                    navigator.navigateToGameOver(
                        score = result.score,
                        bestScore = result.bestScore,
                        missedCount = result.missedCount,
                        maxCombo = result.maxCombo
                    )
                },
                onMainMenu = {
                    navigator.navigateTo(Screen.Start)
                }
            )
        }

        composable(
            route = Screen.GameOver.route,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("bestScore") { type = NavType.IntType },
                navArgument("missedCount") { type = NavType.IntType },
                navArgument("maxCombo") { type = NavType.IntType }
            )
        ) {
            GameOverScreen(
                onPlayAgain = { navigator.navigateToPlaying(ChallengeMode.NORMAL) },
                onMainMenu = { navigator.navigateTo(Screen.Start) }
            )
        }
    }
}
