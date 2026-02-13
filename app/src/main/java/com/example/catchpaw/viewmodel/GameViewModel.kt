package com.example.catchpaw.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor() : ViewModel() {

    var score by mutableIntStateOf(0)
        private set

    var bestScore by mutableIntStateOf(0)
        private set

    var missedCount by mutableIntStateOf(0)
        private set

    fun addScore(points: Int) {
        score += points
    }

    fun incrementMissed() {
        missedCount++
    }

    fun onGameOver() {
        if (score > bestScore) {
            bestScore = score
        }
    }

    fun resetForNewGame() {
        score = 0
        missedCount = 0
    }
}
