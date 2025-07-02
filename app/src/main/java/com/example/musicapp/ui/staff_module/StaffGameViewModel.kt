package com.example.musicapp.ui.staff_module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.use_case.UpdateHighscoreUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ... (Data classes NoteName, GameQuestion, etc. sin cambios)
enum class NoteName { DO, RE, MI, FA, SOL, LA, SI }
data class GameQuestion(val noteName: NoteName, val staffPosition: Int)
enum class StaffGameState { IDLE, PLAYING, FINISHED }
data class StaffGameUiState(
    val gameState: StaffGameState = StaffGameState.IDLE,
    val currentQuestion: GameQuestion? = null,
    val score: Int = 0,
    val lives: Int = 3,
    val timePerQuestion: Long = 5000L,
    val timeRemaining: Float = 1.0f,
    val feedback: String = ""
)

class StaffGameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StaffGameUiState())
    val uiState: StateFlow<StaffGameUiState> = _uiState.asStateFlow()

    private var questionJob: Job? = null

    private val availableNotes = listOf(
        GameQuestion(NoteName.MI, 0), GameQuestion(NoteName.FA, 1),
        GameQuestion(NoteName.SOL, 2), GameQuestion(NoteName.LA, 3),
        GameQuestion(NoteName.SI, 4), GameQuestion(NoteName.DO, 5),
        GameQuestion(NoteName.RE, 6), GameQuestion(NoteName.MI, 7),
        GameQuestion(NoteName.FA, 8), GameQuestion(NoteName.SOL, 9)
    )

    // INYECTAMOS EL NUEVO CASO DE USO
    private val updateHighscoreUseCase = UpdateHighscoreUseCase()

    // ... (startGame, nextQuestion, handleAnswer sin cambios)
    fun startGame() {
        if (_uiState.value.gameState != StaffGameState.IDLE) return
        _uiState.value = StaffGameUiState(gameState = StaffGameState.PLAYING)
        nextQuestion()
    }
    private fun nextQuestion() {
        questionJob?.cancel()
        val newQuestion = availableNotes.random()
        _uiState.update { it.copy(currentQuestion = newQuestion, timeRemaining = 1.0f, feedback = "") }
        questionJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val questionTime = _uiState.value.timePerQuestion
            while (System.currentTimeMillis() - startTime < questionTime) {
                val remaining = (System.currentTimeMillis() - startTime).toFloat() / questionTime
                _uiState.update { it.copy(timeRemaining = 1.0f - remaining) }
                delay(50)
            }
            handleAnswer(null)
        }
    }
    fun handleAnswer(selectedNote: NoteName?) {
        questionJob?.cancel()
        if (_uiState.value.gameState != StaffGameState.PLAYING) return
        val correctAnswer = _uiState.value.currentQuestion?.noteName
        if (selectedNote == correctAnswer) {
            _uiState.update {
                it.copy(
                    score = it.score + 100,
                    feedback = "¡Correcto!",
                    timePerQuestion = (it.timePerQuestion * 0.95f).toLong().coerceAtLeast(1500L)
                )
            }
            viewModelScope.launch {
                delay(1000)
                nextQuestion()
            }
        } else {
            val newLives = _uiState.value.lives - 1
            _uiState.update {
                it.copy(
                    lives = newLives,
                    feedback = if(selectedNote == null) "¡Se acabó el tiempo!" else "¡Incorrecto! Era ${correctAnswer?.name}"
                )
            }
            if (newLives > 0) {
                viewModelScope.launch {
                    delay(1500)
                    nextQuestion()
                }
            } else {
                finishGame()
            }
        }
    }


    // MÉTODO ACTUALIZADO
    private fun finishGame() {
        viewModelScope.launch {
            updateHighscoreUseCase("staff", _uiState.value.score)
            _uiState.update { it.copy(gameState = StaffGameState.FINISHED) }
        }
    }

    fun resetGame() {
        _uiState.value = StaffGameUiState()
    }
}