package com.aysimaerdem.catchpaw.ui.screen.playing

import com.aysimaerdem.catchpaw.domain.model.GameResult

sealed interface PlayingUiEvent {
    data class GameOver(val result: GameResult) : PlayingUiEvent
}
