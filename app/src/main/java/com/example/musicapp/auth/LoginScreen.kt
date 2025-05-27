package com.example.musicapp.auth // Asegúrate que el paquete sea correcto

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.R
import com.example.musicapp.ui.theme.MusicAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // Observa los LiveData del ViewModel y los convierte a State de Compose
    val emailState = loginViewModel.email.observeAsState("")
    val passwordState = loginViewModel.password.observeAsState("")
    val loginUIEventState = loginViewModel.loginEvent.observeAsState() // Renombré para claridad local

    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Efecto para manejar los eventos del ViewModel (efectos secundarios)
    LaunchedEffect(loginUIEventState.value) { // Escucha cambios en el evento
        when (val event = loginUIEventState.value) {
            is LoginUIEvent.Loading -> {
                isLoading = true
            }
            is LoginUIEvent.Success -> {
                isLoading = false
                Toast.makeText(context, "Login Exitoso!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
            is LoginUIEvent.Error -> {
                isLoading = false
                Toast.makeText(context, "Error: ${event.message}", Toast.LENGTH_LONG).show()
            }
            is LoginUIEvent.NavigateToRegister -> {
                isLoading = false
                onNavigateToRegister()
            }
            null -> {
                // Estado inicial o sin evento
                isLoading = false
            }
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplicar padding del Scaffold
                .padding(24.dp), // Padding adicional para el contenido
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher), // Asegúrate que este recurso exista
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )

            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = emailState.value,
                onValueChange = { newValue -> loginViewModel.email.value = newValue }, // Lambda explícita
                label = { Text("Correo Electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { newValue -> loginViewModel.password.value = newValue }, // Lambda explícita
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.LockOpen // Icono para contraseña visible
                    else
                        Icons.Filled.Lock     // Icono para contraseña oculta

                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña" // Puedes ajustar esto

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { loginViewModel.onLoginClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary // Para contraste en el botón
                    )
                } else {
                    Text("Ingresar")
                }
            }

            TextButton(
                onClick = { loginViewModel.onNavigateToRegisterClicked() },
                modifier = Modifier.padding(top = 16.dp),
                enabled = !isLoading
            ) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MusicAppTheme { // Asegúrate que esta sea la forma correcta de llamar a tu tema
        LoginScreen(
            // No necesitas un ViewModel real para el preview básico de UI
            onLoginSuccess = {},
            onNavigateToRegister = {}
        )
    }
}