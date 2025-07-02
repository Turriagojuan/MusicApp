package com.example.musicapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.model.User
import com.example.musicapp.data.repository.UserRepository
import com.example.musicapp.data.repository.UserRepositoryImpl
import com.example.musicapp.data.source.FirebaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val isLoggedOut: Boolean = false
)

class MainViewModel : ViewModel() {

    private val userRepository: UserRepository = UserRepositoryImpl(FirebaseService())

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentUser = userRepository.getCurrentUser()
            if (currentUser != null) {
                _uiState.update { it.copy(isLoading = false, user = currentUser) }
            } else {
                _uiState.update { it.copy(isLoading = false, isLoggedOut = true) }
            }
        }
    }

    fun logout() {
        userRepository.logout()
        _uiState.update { it.copy(isLoggedOut = true) }
    }
}