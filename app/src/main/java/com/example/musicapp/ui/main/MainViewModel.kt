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

// --- INICIO DE CAMBIOS ---
data class MainUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val isLoggedOut: Boolean = false,
    val languageReady: Boolean = false // Nuevo estado para controlar la carga del idioma
)
// --- FIN DE CAMBIOS ---

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
                // Se actualiza el usuario, pero languageReady sigue en false hasta que MainActivity lo confirme.
                _uiState.update { it.copy(isLoading = false, user = currentUser) }
            } else {
                _uiState.update { it.copy(isLoading = false, isLoggedOut = true) }
            }
        }
    }

    // --- INICIO DE CAMBIOS ---
    // Nueva función para que MainActivity nos avise cuando el idioma ya se aplicó.
    fun onLanguageApplied() {
        _uiState.update { it.copy(languageReady = true) }
    }
    // --- FIN DE CAMBIOS ---

    fun logout() {
        userRepository.logout()
        _uiState.update { it.copy(isLoggedOut = true) }
    }
}