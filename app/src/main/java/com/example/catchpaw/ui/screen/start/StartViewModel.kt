package com.catchpaw.ui.screen.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catchpaw.domain.usecase.GetBestScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    getBestScore: GetBestScoreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StartUiState())
    val uiState: StateFlow<StartUiState> = _uiState.asStateFlow()

    init {
        getBestScore()
            .onEach { best ->
                _uiState.value = StartUiState(bestScore = best, isLoading = false)
            }
            .launchIn(viewModelScope)
    }
}
