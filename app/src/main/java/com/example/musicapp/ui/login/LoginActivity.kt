package com.example.musicapp.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.musicapp.R
import com.example.musicapp.ui.main.MainActivity
import com.example.musicapp.ui.theme.MusicAppTheme

class LoginActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicAppTheme {
                val uiState by loginViewModel.uiState.collectAsState()

                if (uiState.loginSuccess) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                LoginScreen(
                    uiState = uiState,
                    onLoginClicked = { email, password ->
                        loginViewModel.login(email, password)
                    },
                    onSignUpClicked = { username, email, password ->
                        loginViewModel.signUp(username, email, password)
                    },
                    onToggleRegistration = {
                        loginViewModel.toggleRegistrationView()
                    }
                )

                uiState.errorMessage?.let { message ->
                    val snackbarHostState = remember { SnackbarHostState() }
                    LaunchedEffect(snackbarHostState, message) {
                        snackbarHostState.showSnackbar(message)
                        loginViewModel.errorMessageShown()
                    }
                    SnackbarHost(hostState = snackbarHostState)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onLoginClicked: (String, String) -> Unit,
    onSignUpClicked: (String, String, String) -> Unit,
    onToggleRegistration: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (uiState.showRegistration) stringResource(id = R.string.register_title) else stringResource(id = R.string.login_title),
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.showRegistration) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(stringResource(id = R.string.username_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(id = R.string.email_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(id = R.string.password_label)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        if (uiState.showRegistration) {
                            onSignUpClicked(username, email, password)
                        } else {
                            onLoginClicked(email, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (uiState.showRegistration) stringResource(id = R.string.register_button) else stringResource(id = R.string.login_button))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (uiState.showRegistration) stringResource(id = R.string.has_account_prompt) else stringResource(id = R.string.no_account_prompt),
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onToggleRegistration() }
            )
        }
    }
}