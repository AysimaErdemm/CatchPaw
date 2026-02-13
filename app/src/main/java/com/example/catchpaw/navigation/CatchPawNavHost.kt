package com.example.catchpaw.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.catchpaw.GameOverScreen
import com.example.catchpaw.PlayingScreen
import com.example.catchpaw.StartScreen
import com.example.catchpaw.viewmodel.GameViewModel

@Composable
fun CatchPawNavHost(
    navController: NavHostController,
    viewModel: GameViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.START,
        enterTransition = {
            fadeIn(tween(400)) + scaleIn(tween(400), initialScale = 0.92f)
        },
        exitTransition = {
            fadeOut(tween(300)) + scaleOut(tween(300), targetScale = 1.05f)
        }
    ) {
        composable(Routes.START) {
            StartScreen(
                bestScore = viewModel.bestScore,
                onStartGame = {
                    viewModel.resetForNewGame()
                    navController.navigate(Routes.PLAYING) {
                        popUpTo(Routes.START) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PLAYING) {
            PlayingScreen(
                viewModel = viewModel,
                onGameOver = {
                    viewModel.onGameOver()
                    navController.navigate(Routes.GAME_OVER) {
                        popUpTo(Routes.START) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.GAME_OVER) {
            GameOverScreen(
                score = viewModel.score,
                bestScore = viewModel.bestScore,
                missedCount = viewModel.missedCount,
                onPlayAgain = {
                    viewModel.resetForNewGame()
                    navController.navigate(Routes.PLAYING) {
                        popUpTo(Routes.START) { inclusive = true }
                    }
                },
                onMainMenu = {
                    navController.navigate(Routes.START) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
