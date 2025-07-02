package com.example.musicapp.ui.login

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

data class LoginUiState(
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val errorMessage: String? = null,
    val showRegistration: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val userRepository: UserRepository = UserRepositoryImpl(FirebaseService())

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Correo y contraseña no pueden estar vacíos.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val user = userRepository.login(email, password)
            if (user != null) {
                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error al iniciar sesión. Verifica tus credenciales.") }
            }
        }
    }

    fun signUp(username: String, email: String, password: String) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Todos los campos son obligatorios.") }
            return
        }

        // --- NUEVA VALIDACIÓN ---
        // Se comprueba la longitud de la contraseña antes de llamar a Firebase.
        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres.") }
            return
        }
        // --- FIN DE LA VALIDACIÓN ---

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val newUser = User(email = email, username = username)
            val user = userRepository.signUp(newUser, password)
            if (user != null) {
                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error en el registro. El correo podría ya estar en uso.") }
            }
        }
    }

    fun toggleRegistrationView() {
        _uiState.update { it.copy(showRegistration = !it.showRegistration, errorMessage = null) }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}