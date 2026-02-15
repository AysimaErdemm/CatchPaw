package com.example.catchpaw.ui.screen.gameover

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catchpaw.domain.usecase.SaveGameResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameOverViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saveGameResult: SaveGameResultUseCase
) : ViewModel() {

    private val score: Int = savedStateHandle["score"] ?: 0
    private val bestScore: Int = savedStateHandle["bestScore"] ?: 0
    private val missedCount: Int = savedStateHandle["missedCount"] ?: 0
    private val maxCombo: Int = savedStateHandle["maxCombo"] ?: 0

    private val _uiState = MutableStateFlow(
        GameOverUiState(
            score = score,
            bestScore = bestScore,
            missedCount = missedCount,
            maxCombo = maxCombo,
            isNewBest = score > 0 && score >= bestScore
        )
    )
    val uiState: StateFlow<GameOverUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            saveGameResult(score, missedCount)
        }
    }
}
