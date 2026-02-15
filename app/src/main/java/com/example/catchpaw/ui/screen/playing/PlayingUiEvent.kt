package com.catchpaw.ui.screen.playing

import com.catchpaw.domain.model.GameResult

sealed interface PlayingUiEvent {
    data class GameOver(val result: GameResult) : PlayingUiEvent
}
