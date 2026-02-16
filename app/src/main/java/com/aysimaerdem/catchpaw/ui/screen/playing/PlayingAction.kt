package com.aysimaerdem.catchpaw.ui.screen.playing

sealed interface PlayingAction {
    data class SetContainerSize(val width: Float, val height: Float, val topBarHeightPx: Float) : PlayingAction
    data class MouseClicked(val mouseId: Int) : PlayingAction
    data object TogglePause : PlayingAction
    data class BombClicked(val bombId: Int) : PlayingAction
    data object Restart : PlayingAction
}
