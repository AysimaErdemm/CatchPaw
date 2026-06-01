package com.aysimaerdem.catchpaw.ui.screen.playing

import com.aysimaerdem.catchpaw.domain.model.GameResult

sealed interface PlayingUiEvent {
    data class GameOver(val result: GameResult) : PlayingUiEvent
    data object PlayCatchSound : PlayingUiEvent
    data object PlayBonusCatchSound : PlayingUiEvent
    data object PlayBombSound : PlayingUiEvent
    data object PlayPowerUpSound : PlayingUiEvent
    data object VibrateLight : PlayingUiEvent
    data object VibrateMedium : PlayingUiEvent
}
