package com.example.musicapp.ui.lessons

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.use_case.UpdateLessonStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LessonUiState(
    val lessonContent: LessonContent? = null,
    val currentPage: Int = 0,
    val isLessonComplete: Boolean = false,
    val isLoading: Boolean = false
)

class LessonViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Obtiene el lessonId de los argumentos de navegación
    private val lessonId: String = checkNotNull(savedStateHandle["lessonId"])
    private val updateLessonStatusUseCase = UpdateLessonStatusUseCase()

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    init {
        loadLessonContent()
    }

    private fun loadLessonContent() {
        _uiState.update { it.copy(lessonContent = LessonContentProvider.getLesson(lessonId)) }
    }

    fun nextPage() {
        val lesson = _uiState.value.lessonContent ?: return
        val currentPage = _uiState.value.currentPage
        if (currentPage < lesson.pages.size - 1) {
            _uiState.update { it.copy(currentPage = currentPage + 1) }
        }
    }

    fun previousPage() {
        val currentPage = _uiState.value.currentPage
        if (currentPage > 0) {
            _uiState.update { it.copy(currentPage = currentPage - 1) }
        }
    }

    fun completeLesson() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = updateLessonStatusUseCase(lessonId, true)
            if (success) {
                _uiState.update { it.copy(isLoading = false, isLessonComplete = true) }
            } else {
                // Manejar error si es necesario
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}