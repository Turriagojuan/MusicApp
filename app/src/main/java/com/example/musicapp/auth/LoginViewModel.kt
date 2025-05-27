package com.example.musicapp.auth // Asegúrate que el paquete sea correcto

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    // Para comunicar eventos de navegación o estado a la Composable UI
    private val _loginEvent = MutableLiveData<LoginUIEvent>()
    val loginEvent: LiveData<LoginUIEvent> = _loginEvent // Exponer como LiveData inmutable

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        // Puedes inicializar con valores de prueba si deseas agilizar el testeo
        // email.value = "test@example.com"
        // password.value = "123456"
        email.value = ""
        password.value = ""
    }

    fun onLoginClicked() {
        val currentEmail = email.value
        val currentPassword = password.value

        if (currentEmail.isNullOrBlank() || currentPassword.isNullOrBlank()) {
            _loginEvent.value = LoginUIEvent.Error("Correo y contraseña no pueden estar vacíos.")
            return
        }

        _loginEvent.value = LoginUIEvent.Loading // Indicar que el proceso de login ha comenzado

        viewModelScope.launch { // Usar viewModelScope para coroutines ligadas al ciclo de vida del ViewModel
            try {
                firebaseAuth.signInWithEmailAndPassword(currentEmail, currentPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("LoginViewModel", "Login exitoso: $currentEmail")
                            _loginEvent.value = LoginUIEvent.Success
                        } else {
                            Log.w("LoginViewModel", "Login fallido", task.exception)
                            _loginEvent.value = LoginUIEvent.Error(task.exception?.message ?: "Error desconocido en el login")
                        }
                    }
            } catch (e: Exception) { // Capturar cualquier otra excepción durante el proceso
                Log.e("LoginViewModel", "Excepción en login", e)
                _loginEvent.value = LoginUIEvent.Error(e.message ?: "Excepción durante el login")
            }
        }
    }

    // Llamado cuando el usuario quiere ir a la pantalla de registro
    fun onNavigateToRegisterClicked() {
        _loginEvent.value = LoginUIEvent.NavigateToRegister
    }
}

// Clase sellada para representar los diferentes estados/eventos de la UI del login
sealed class LoginUIEvent {
    object Loading : LoginUIEvent()
    object Success : LoginUIEvent()
    object NavigateToRegister : LoginUIEvent() // Nuevo evento para navegación
    data class Error(val message: String) : LoginUIEvent()
}