package com.aysimaerdem.catchpaw.ui.screen.playing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aysimaerdem.catchpaw.data.local.ChallengePreference
import com.aysimaerdem.catchpaw.domain.engine.GameEngine
import com.aysimaerdem.catchpaw.domain.model.ActivePowerUp
import com.aysimaerdem.catchpaw.domain.model.Bomb
import com.aysimaerdem.catchpaw.domain.model.ChallengeMode
import com.aysimaerdem.catchpaw.domain.model.ExplosionEffect
import com.aysimaerdem.catchpaw.domain.model.GameConfig
import com.aysimaerdem.catchpaw.domain.model.Mouse
import com.aysimaerdem.catchpaw.domain.model.MouseType
import com.aysimaerdem.catchpaw.domain.model.PawEffect
import com.aysimaerdem.catchpaw.domain.model.PowerUp
import com.aysimaerdem.catchpaw.domain.model.PowerUpType
import com.aysimaerdem.catchpaw.domain.usecase.GetBestScoreUseCase
import com.aysimaerdem.catchpaw.domain.usecase.SaveGameResultUseCase
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
    private val getBestScore: GetBestScoreUseCase,
    private val saveGameResult: SaveGameResultUseCase,
    private val challengePreference: ChallengePreference,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val challengeMode: ChallengeMode = ChallengeMode.entries[
        savedStateHandle.get<Int>("mode") ?: ChallengeMode.NORMAL.ordinal
    ]

    private val gameDuration: Long = when (challengeMode) {
        ChallengeMode.NORMAL -> GameConfig.GAME_DURATION_MS
        ChallengeMode.DAILY -> GameConfig.DAILY_DURATION_MS
    }

    private val difficultyCap: Int = when (challengeMode) {
        ChallengeMode.NORMAL -> GameConfig.DIFFICULTY_SCORE_CAP
        ChallengeMode.DAILY -> GameConfig.DAILY_DIFFICULTY_CAP
    }

    private var engine = GameEngine(difficultyCap = difficultyCap)

    private val _uiState = MutableStateFlow(PlayingUiState(timeLeftMs = gameDuration, totalDurationMs = gameDuration))
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
    private val powerUps = mutableListOf<PowerUp>()
    private val activePowerUps = mutableListOf<ActivePowerUp>()

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
            is PlayingAction.PowerUpClicked -> onPowerUpClicked(action.powerUpId)
            is PlayingAction.TogglePause -> togglePause()
            is PlayingAction.Restart -> restart()
        }
    }

    private fun onMouseClicked(mouseId: Int) {
        if (isPaused) return
        val mouse = mice.find { it.id == mouseId } ?: return
        mice.remove(mouse)

        val now = System.currentTimeMillis()
        val isDoublePoints = activePowerUps.any { it.type == PowerUpType.DOUBLE_POINTS && it.endsAtMs > now }
        val basePoints = mouse.type.basePoints * (if (isDoublePoints) 2 else 1)
        engine.onMouseCaught(now, basePoints)

        val paw = PawEffect(id = pawIdCounter++, x = mouse.x, y = mouse.y)
        _uiState.update { state ->
            state.copy(
                score = engine.score,
                combo = engine.combo,
                mice = mice.toList(),
                pawEffects = state.pawEffects + paw
            )
        }

        viewModelScope.launch {
            if (mouse.type == MouseType.BONUS) {
                _events.send(PlayingUiEvent.PlayBonusCatchSound)
            } else {
                _events.send(PlayingUiEvent.PlayCatchSound)
            }
            _events.send(PlayingUiEvent.VibrateLight)
        }
    }

    private fun onBombClicked(bombId: Int) {
        if (isPaused) return
        val bomb = bombs.find { it.id == bombId } ?: return

        val now = System.currentTimeMillis()
        val hasShield = activePowerUps.any { it.type == PowerUpType.SHIELD && it.endsAtMs > now }
        bombs.remove(bomb)

        if (hasShield) {
            activePowerUps.removeAll { it.type == PowerUpType.SHIELD }
            _uiState.update { state ->
                state.copy(bombs = bombs.toList(), activePowerUps = activePowerUps.toList())
            }
        } else {
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

            viewModelScope.launch {
                _events.send(PlayingUiEvent.PlayBombSound)
                _events.send(PlayingUiEvent.VibrateMedium)
            }
        }
    }

    private fun onPowerUpClicked(powerUpId: Int) {
        if (isPaused) return
        val powerUp = powerUps.find { it.id == powerUpId } ?: return
        powerUps.remove(powerUp)

        val now = System.currentTimeMillis()
        val active = ActivePowerUp(type = powerUp.type, endsAtMs = now + powerUp.type.durationMs)
        activePowerUps.removeAll { it.type == powerUp.type }
        activePowerUps.add(active)

        _uiState.update { state ->
            state.copy(powerUps = powerUps.toList(), activePowerUps = activePowerUps.toList())
        }

        viewModelScope.launch {
            _events.send(PlayingUiEvent.PlayPowerUpSound)
            _events.send(PlayingUiEvent.VibrateLight)
        }
    }

    private fun togglePause() {
        if (isPaused) {
            timerStartTime = System.currentTimeMillis() - elapsedBeforePause
            isPaused = false
            _uiState.update { it.copy(isPaused = false) }
        } else {
            elapsedBeforePause = System.currentTimeMillis() - timerStartTime
            isPaused = true
            _uiState.update { it.copy(isPaused = true) }
        }
    }

    private fun restart() {
        gameLoopJob?.cancel()
        engine = GameEngine(difficultyCap = difficultyCap)
        mice.clear()
        bombs.clear()
        powerUps.clear()
        activePowerUps.clear()
        pawIdCounter = 0
        explosionIdCounter = 0
        isPaused = false
        elapsedBeforePause = 0L
        _uiState.value = PlayingUiState(timeLeftMs = gameDuration, totalDurationMs = gameDuration)
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
            launch { runActivePowerUpCleanup() }
        }
    }

    private suspend fun waitWhilePaused() {
        while (isPaused) delay(50)
    }

    private suspend fun runTimer() {
        while (true) {
            delay(50)
            if (isPaused) { waitWhilePaused(); continue }
            val now = System.currentTimeMillis()
            val isFrozen = activePowerUps.any { it.type == PowerUpType.TIME_FREEZE && it.endsAtMs > now }
            if (isFrozen) {
                timerStartTime += 50
            }
            val elapsed = now - timerStartTime
            val timeLeft = (gameDuration - elapsed).coerceAtLeast(0)
            _uiState.update { it.copy(timeLeftMs = timeLeft) }
            if (timeLeft <= 0) {
                finishGame()
                break
            }
        }
    }

    private suspend fun finishGame() {
        when (challengeMode) {
            ChallengeMode.NORMAL -> {
                val bestScore = getBestScore().first()
                val result = engine.buildGameResult(bestScore)
                saveGameResult(result.score, result.missedCount)
                _events.send(PlayingUiEvent.GameOver(result))
            }
            ChallengeMode.DAILY -> {
                val prevBest = challengePreference.getDailyBestScore()
                val result = engine.buildGameResult(prevBest)
                // Always save to mark today as played; keeps the higher score
                challengePreference.setDailyBestScore(result.bestScore)
                _events.send(PlayingUiEvent.GameOver(result))
            }
        }
    }

    private suspend fun runSpawner() {
        delay(500)
        while (true) {
            if (isPaused) { waitWhilePaused(); continue }
            if (mice.size < engine.calculateMaxMice() && containerWidth > 0 && containerHeight > 0) {
                when {
                    engine.shouldSpawnPowerUp() -> {
                        val newPowerUp = engine.createPowerUp(containerWidth, containerHeight, topBarHeightPx)
                        powerUps.add(newPowerUp)
                        _uiState.update { it.copy(powerUps = powerUps.toList()) }
                    }
                    engine.shouldSpawnBomb() -> {
                        val newBomb = engine.createBomb(containerWidth, containerHeight, topBarHeightPx)
                        bombs.add(newBomb)
                        _uiState.update { it.copy(bombs = bombs.toList()) }
                    }
                    else -> {
                        val newMouse = engine.createMouse(containerWidth, containerHeight, topBarHeightPx)
                        mice.add(newMouse)
                        _uiState.update { it.copy(mice = mice.toList()) }
                    }
                }
            }
            delay(engine.calculateSpawnInterval())
        }
    }

    private suspend fun runMarkDying() {
        while (true) {
            delay(100)
            if (isPaused) { waitWhilePaused(); continue }
            val now = System.currentTimeMillis()
            var changed = false

            mice.indices.forEach { i ->
                val mouse = mice[i]
                if (!mouse.isDying && now - mouse.spawnTimeMs >= mouse.lifetimeMs) {
                    mice[i] = mouse.copy(isDying = true, dyingStartTime = now)
                    changed = true
                }
            }

            bombs.indices.forEach { i ->
                val bomb = bombs[i]
                if (!bomb.isDying && now - bomb.spawnTimeMs >= GameConfig.BOMB_LIFETIME_MS) {
                    bombs[i] = bomb.copy(isDying = true, dyingStartTime = now)
                    changed = true
                }
            }

            powerUps.indices.forEach { i ->
                val powerUp = powerUps[i]
                if (!powerUp.isDying && now - powerUp.spawnTimeMs >= GameConfig.POWER_UP_LIFETIME_MS) {
                    powerUps[i] = powerUp.copy(isDying = true, dyingStartTime = now)
                    changed = true
                }
            }

            if (changed) {
                _uiState.update { it.copy(mice = mice.toList(), bombs = bombs.toList(), powerUps = powerUps.toList()) }
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

            val deadPowerUps = powerUps.filter { it.isDying && now - it.dyingStartTime > GameConfig.MOUSE_FADE_OUT_MS }
            if (deadPowerUps.isNotEmpty()) {
                powerUps.removeAll(deadPowerUps.toSet())
                changed = true
            }

            if (changed) {
                _uiState.update {
                    it.copy(
                        mice = mice.toList(),
                        bombs = bombs.toList(),
                        powerUps = powerUps.toList(),
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
                if (state.pawEffects.isNotEmpty()) state.copy(pawEffects = state.pawEffects.drop(1))
                else state
            }
        }
    }

    private suspend fun runExplosionCleanup() {
        while (true) {
            delay(500)
            _uiState.update { state ->
                if (state.explosionEffects.isNotEmpty()) state.copy(explosionEffects = state.explosionEffects.drop(1))
                else state
            }
        }
    }

    private suspend fun runActivePowerUpCleanup() {
        while (true) {
            delay(200)
            val now = System.currentTimeMillis()
            val sizeBefore = activePowerUps.size
            activePowerUps.removeAll { it.endsAtMs <= now }
            if (activePowerUps.size != sizeBefore) {
                _uiState.update { it.copy(activePowerUps = activePowerUps.toList()) }
            }
        }
    }
}
