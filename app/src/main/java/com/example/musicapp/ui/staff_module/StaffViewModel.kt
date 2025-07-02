package com.example.musicapp.ui.staff_module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.model.StaffModuleProgress
import com.example.musicapp.domain.use_case.GetUserProgressUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StaffModuleUiState(
    val isLoading: Boolean = true,
    val progress: StaffModuleProgress? = null
)

class StaffViewModel : ViewModel() {

    private val getUserProgressUseCase = GetUserProgressUseCase()

    private val _uiState = MutableStateFlow(StaffModuleUiState())
    val uiState: StateFlow<StaffModuleUiState> = _uiState.asStateFlow()

    init {
        loadProgress()
    }

    private fun loadProgress() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userProgress = getUserProgressUseCase()
            _uiState.update {
                it.copy(isLoading = false, progress = userProgress?.staffModule)
            }
        }
    }
}