package com.example.catchpaw.ui.screen.playing

import com.example.catchpaw.domain.model.GameResult

sealed interface PlayingUiEvent {
    data class GameOver(val result: GameResult) : PlayingUiEvent
}
