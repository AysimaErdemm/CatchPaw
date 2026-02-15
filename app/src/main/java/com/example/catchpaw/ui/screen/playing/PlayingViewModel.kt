package com.example.catchpaw.ui.screen.playing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catchpaw.domain.engine.GameEngine
import com.example.catchpaw.domain.model.Bomb
import com.example.catchpaw.domain.model.ExplosionEffect
import com.example.catchpaw.domain.model.GameConfig
import com.example.catchpaw.domain.model.Mouse
import com.example.catchpaw.domain.model.PawEffect
import com.example.catchpaw.domain.usecase.GetBestScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayingViewModel @Inject constructor(
    private val getBestScore: GetBestScoreUseCase
) : ViewModel() {

    private var engine = GameEngine()

    private val _uiState = MutableStateFlow(PlayingUiState(timeLeftMs = GameConfig.GAME_DURATION_MS))
    val uiState: StateFlow<PlayingUiState> = _uiState.asStateFlow()

    private val _events = Channel<PlayingUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var containerWidth = 0f
    private var containerHeight = 0f
    private var topBarHeightPx = GameConfig.TOP_BAR_HEIGHT
    private var pawIdCounter = 0
    private var explosionIdCounter = 0
    private val mice = mutableListOf<Mouse>()
    private val bombs = mutableListOf<Bomb>()

    @Volatile
    private var isPaused = false
    private var elapsedBeforePause = 0L
    private var timerStartTime = 0L

    private var gameLoopJob: Job? = null

    init {
        startGameLoops()
    }

    fun onAction(action: PlayingAction) {
        when (action) {
            is PlayingAction.SetContainerSize -> {
                containerWidth = action.width
                containerHeight = action.height
                topBarHeightPx = action.topBarHeightPx
            }
            is PlayingAction.MouseClicked -> onMouseClicked(action.mouseId)
            is PlayingAction.BombClicked -> onBombClicked(action.bombId)
            is PlayingAction.TogglePause -> togglePause()
            is PlayingAction.Restart -> restart()
        }
    }

    private fun onMouseClicked(mouseId: Int) {
        if (isPaused) return
        val mouse = mice.find { it.id == mouseId } ?: return

        mice.remove(mouse)
        engine.onMouseCaught(System.currentTimeMillis())

        val paw = PawEffect(id = pawIdCounter++, x = mouse.x, y = mouse.y)
        _uiState.update { state ->
            state.copy(
                score = engine.score,
                combo = engine.combo,
                mice = mice.toList(),
                pawEffects = state.pawEffects + paw
            )
        }
    }

    private fun onBombClicked(bombId: Int) {
        if (isPaused) return
        val bomb = bombs.find { it.id == bombId } ?: return

        bombs.remove(bomb)
        engine.onBombClicked()
        timerStartTime -= GameConfig.BOMB_PENALTY_MS

        val explosion = ExplosionEffect(id = explosionIdCounter++, x = bomb.x, y = bomb.y)
        _uiState.update { state ->
            state.copy(
                combo = engine.combo,
                bombs = bombs.toList(),
                explosionEffects = state.explosionEffects + explosion
            )
        }
    }

    private fun togglePause() {
        if (isPaused) {
            // Resume — adjust timer start so elapsed stays correct
            timerStartTime = System.currentTimeMillis() - elapsedBeforePause
            isPaused = false
            _uiState.update { it.copy(isPaused = false) }
        } else {
            // Pause — save elapsed
            elapsedBeforePause = System.currentTimeMillis() - timerStartTime
            isPaused = true
            _uiState.update { it.copy(isPaused = true) }
        }
    }

    private fun restart() {
        gameLoopJob?.cancel()
        engine = GameEngine()
        mice.clear()
        bombs.clear()
        pawIdCounter = 0
        explosionIdCounter = 0
        isPaused = false
        elapsedBeforePause = 0L
        _uiState.value = PlayingUiState(timeLeftMs = GameConfig.GAME_DURATION_MS)
        startGameLoops()
    }

    private fun startGameLoops() {
        timerStartTime = System.currentTimeMillis()
        gameLoopJob = viewModelScope.launch {
            launch { runTimer() }
            launch { runSpawner() }
            launch { runMarkDying() }
            launch { runRemoveDead() }
            launch { runPawCleanup() }
            launch { runExplosionCleanup() }
        }
    }

    private suspend fun waitWhilePaused() {
        while (isPaused) delay(50)
    }

    private suspend fun runTimer() {
        while (true) {
            delay(50)
            if (isPaused) { waitWhilePaused(); continue }
            val elapsed = System.currentTimeMillis() - timerStartTime
            val timeLeft = (GameConfig.GAME_DURATION_MS - elapsed).coerceAtLeast(0)
            _uiState.update { it.copy(timeLeftMs = timeLeft) }
            if (timeLeft <= 0) {
                val bestScore = getBestScore().first()
                val result = engine.buildGameResult(bestScore)
                _events.send(PlayingUiEvent.GameOver(result))
                break
            }
        }
    }

    private suspend fun runSpawner() {
        delay(500)
        while (true) {
            if (isPaused) { waitWhilePaused(); continue }
            if (mice.size < engine.calculateMaxMice() && containerWidth > 0 && containerHeight > 0) {
                if (engine.shouldSpawnBomb()) {
                    val newBomb = engine.createBomb(containerWidth, containerHeight, topBarHeightPx)
                    bombs.add(newBomb)
                    _uiState.update { it.copy(bombs = bombs.toList()) }
                } else {
                    val newMouse = engine.createMouse(containerWidth, containerHeight, topBarHeightPx)
                    mice.add(newMouse)
                    _uiState.update { it.copy(mice = mice.toList()) }
                }
            }
            delay(engine.calculateSpawnInterval())
        }
    }

    private suspend fun runMarkDying() {
        while (true) {
            delay(engine.calculateMouseLifetime())
            if (isPaused) { waitWhilePaused(); continue }
            val now = System.currentTimeMillis()
            var changed = false

            val aliveMouse = mice.firstOrNull { !it.isDying }
            if (aliveMouse != null) {
                val index = mice.indexOf(aliveMouse)
                if (index >= 0) {
                    mice[index] = aliveMouse.copy(isDying = true, dyingStartTime = now)
                    changed = true
                }
            }

            val aliveBomb = bombs.firstOrNull { !it.isDying }
            if (aliveBomb != null) {
                val bIndex = bombs.indexOf(aliveBomb)
                if (bIndex >= 0) {
                    bombs[bIndex] = aliveBomb.copy(isDying = true, dyingStartTime = now)
                    changed = true
                }
            }

            if (changed) {
                _uiState.update {
                    it.copy(mice = mice.toList(), bombs = bombs.toList())
                }
            }
        }
    }

    private suspend fun runRemoveDead() {
        while (true) {
            delay(50)
            if (isPaused) continue
            val now = System.currentTimeMillis()
            var changed = false

            val deadMice = mice.filter { it.isDying && now - it.dyingStartTime > GameConfig.MOUSE_FADE_OUT_MS }
            if (deadMice.isNotEmpty()) {
                deadMice.forEach { engine.onMouseMissed() }
                mice.removeAll(deadMice.toSet())
                changed = true
            }

            val deadBombs = bombs.filter { it.isDying && now - it.dyingStartTime > GameConfig.MOUSE_FADE_OUT_MS }
            if (deadBombs.isNotEmpty()) {
                bombs.removeAll(deadBombs.toSet())
                changed = true
            }

            if (changed) {
                _uiState.update {
                    it.copy(
                        mice = mice.toList(),
                        bombs = bombs.toList(),
                        combo = engine.combo
                    )
                }
            }
        }
    }

    private suspend fun runPawCleanup() {
        while (true) {
            delay(500)
            _uiState.update { state ->
                if (state.pawEffects.isNotEmpty()) {
                    state.copy(pawEffects = state.pawEffects.drop(1))
                } else {
                    state
                }
            }
        }
    }

    private suspend fun runExplosionCleanup() {
        while (true) {
            delay(500)
            _uiState.update { state ->
                if (state.explosionEffects.isNotEmpty()) {
                    state.copy(explosionEffects = state.explosionEffects.drop(1))
                } else {
                    state
                }
            }
        }
    }
}
