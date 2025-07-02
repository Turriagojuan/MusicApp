package com.example.musicapp.ui.rhythm_module

import android.media.ToneGenerator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.use_case.UpdateHighscoreUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- Data classes and Enums (Moved to top-level for visibility) ---

data class RhythmicPattern(val sequence: List<Int>) // 0 for rest, 1 for beat

enum class GamePhase {
    LISTEN, // App plays the pattern
    PLAY,   // Player imitates the pattern
    EVALUATION // Round result is shown
}

enum class GameState {
    IDLE,
    PLAYING,
    FINISHED
}

data class RhythmGameState(
    val gameState: GameState = GameState.IDLE,
    val gamePhase: GamePhase = GamePhase.LISTEN,
    val currentPattern: RhythmicPattern? = null,
    val playerTaps: MutableList<Long> = mutableListOf(),
    val round: Int = 1,
    val score: Int = 0,
    val feedback: String = "",
    // Índice para la animación visual, controlado por el ViewModel
    val activeBeatIndex: Int = -1
)

enum class SoundEvent(val tone: Int) {
    METRONOME_CLICK(ToneGenerator.TONE_CDMA_PIP),
    SUCCESS(ToneGenerator.TONE_PROP_ACK),
    FAILURE(ToneGenerator.TONE_PROP_NACK)
}

class RhythmGameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RhythmGameState())
    val uiState: StateFlow<RhythmGameState> = _uiState.asStateFlow()

    private val _soundEvents = MutableSharedFlow<SoundEvent>()
    val soundEvents = _soundEvents.asSharedFlow()

    private val updateHighscoreUseCase = UpdateHighscoreUseCase()
    private var gameJob: Job? = null
    private val beatInterval = 500L

    // Guardará el momento exacto en que empieza el turno del jugador
    private var playPhaseStartTime: Long = 0L

    private val patterns = listOf(
        RhythmicPattern(listOf(1, 0, 1, 0)),
        RhythmicPattern(listOf(1, 1, 0, 0)),
        RhythmicPattern(listOf(1, 1, 1, 1)),
        RhythmicPattern(listOf(1, 0, 1, 1)),
        RhythmicPattern(listOf(1, 1, 0, 1))
    )

    fun startGame() {
        if (_uiState.value.gameState != GameState.IDLE) return
        _uiState.value = RhythmGameState(gameState = GameState.PLAYING)
        startNewRound()
    }

    private fun startNewRound() {
        gameJob?.cancel()
        val newPattern = patterns.random()
        _uiState.update { it.copy(
            currentPattern = newPattern,
            gamePhase = GamePhase.LISTEN,
            playerTaps = mutableListOf(),
            feedback = "¡Escucha!"
        )}

        // Lógica de ritmo centralizada
        gameJob = viewModelScope.launch {
            delay(1000)
            newPattern.sequence.forEachIndexed { index, step ->
                // Actualiza el índice visual
                _uiState.update { it.copy(activeBeatIndex = index) }
                if (step == 1) {
                    _soundEvents.emit(SoundEvent.METRONOME_CLICK)
                }
                delay(beatInterval)
            }

            // Resetea el visualizador y empieza el turno del jugador
            _uiState.update { it.copy(activeBeatIndex = -1) }
            playPhaseStartTime = System.currentTimeMillis()
            _uiState.update { it.copy(gamePhase = GamePhase.PLAY, feedback = "¡Tu turno!") }
        }
    }

    fun onBeatTapped() {
        if (_uiState.value.gameState != GameState.PLAYING || _uiState.value.gamePhase != GamePhase.PLAY) return

        // Registra el tiempo del toque relativo al inicio del turno
        val tapTime = System.currentTimeMillis() - playPhaseStartTime
        _uiState.value.playerTaps.add(tapTime)

        val patternSize = _uiState.value.currentPattern?.sequence?.count { it == 1 } ?: 0
        if (_uiState.value.playerTaps.size >= patternSize) {
            evaluateTaps()
        }
    }

    private fun evaluateTaps() {
        gameJob?.cancel()
        val pattern = _uiState.value.currentPattern ?: return
        val playerTaps = _uiState.value.playerTaps
        val expectedTapTimes = mutableListOf<Long>()

        // Calcula los momentos "perfectos" para los toques según el reloj del juego
        var cumulativeTime = 0L
        pattern.sequence.forEach { step ->
            if (step == 1) {
                expectedTapTimes.add(cumulativeTime)
            }
            cumulativeTime += beatInterval
        }

        var correctTaps = 0
        // Aumentamos la tolerancia para que sea más flexible y justa
        val tolerance = 500L

        // Compara cada toque del jugador con el tiempo perfecto esperado
        for (i in expectedTapTimes.indices) {
            if (i < playerTaps.size) {
                if (kotlin.math.abs(expectedTapTimes[i] - playerTaps[i]) < tolerance) {
                    correctTaps++
                }
            }
        }

        val accuracy = if (expectedTapTimes.isNotEmpty()) correctTaps.toFloat() / expectedTapTimes.size else 0f

        _uiState.update { it.copy(gamePhase = GamePhase.EVALUATION) }

        viewModelScope.launch {
            if (accuracy > 0.6) { // Requisito más flexible: 60% de acierto
                _soundEvents.emit(SoundEvent.SUCCESS)
                _uiState.update { state ->
                    state.copy(
                        score = state.score + 100,
                        round = state.round + 1,
                        feedback = "¡Genial!"
                    )
                }
            } else {
                _soundEvents.emit(SoundEvent.FAILURE)
                _uiState.update { state -> state.copy(feedback = "¡Casi! Inténtalo de nuevo.") }
            }
            delay(2000)

            if (_uiState.value.round > 5) {
                finishGame()
            } else {
                startNewRound()
            }
        }
    }

    private fun finishGame() {
        viewModelScope.launch {
            updateHighscoreUseCase("rhythm", _uiState.value.score)
            _uiState.update { it.copy(gameState = GameState.FINISHED) }
        }
    }

    fun resetGame() {
        gameJob?.cancel()
        _uiState.value = RhythmGameState()
    }
}