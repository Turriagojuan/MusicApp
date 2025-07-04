package com.example.musicapp.ui.main

import android.Manifest // <-- AÑADE ESTA IMPORTACIÓN
import android.content.Intent
import android.content.pm.PackageManager // <-- AÑADE ESTA IMPORTACIÓN
import android.os.Build // <-- AÑADE ESTA IMPORTACIÓN
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts // <-- AÑADE ESTA IMPORTACIÓN
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat // <-- AÑADE ESTA IMPORTACIÓN
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.example.musicapp.notifications.NotificationScheduler
import com.example.musicapp.ui.login.LoginActivity
import com.example.musicapp.ui.navigation.AppNavigation
import com.example.musicapp.ui.rhythm_module.RhythmGameActivity
import com.example.musicapp.ui.staff_module.StaffGameActivity
import com.example.musicapp.ui.theme.MusicAppTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    // --- INICIO DE CAMBIOS ---
    // Se crea un lanzador para solicitar permisos.
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permiso concedido.
        } else {
            // Permiso denegado. Podrías mostrar un mensaje al usuario.
        }
    }

    private fun askNotificationPermission() {
        // Esta función solo se ejecuta en Android 13 (API 33) o superior.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Se solicita el permiso.
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    // --- FIN DE CAMBIOS ---

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- INICIO DE CAMBIOS ---
        // Se solicita el permiso al crear la actividad.
        askNotificationPermission()
        // --- FIN DE CAMBIOS ---

        // (El resto de tu código en onCreate se mantiene igual)
        mainViewModel.uiState
            .onEach { state ->
                state.user?.language?.let { lang ->
                    val appLocale = LocaleListCompat.forLanguageTags(lang)
                    if (AppCompatDelegate.getApplicationLocales() != appLocale) {
                        AppCompatDelegate.setApplicationLocales(appLocale)
                    }
                    mainViewModel.onLanguageApplied()
                }
            }
            .distinctUntilChanged { old, new -> old.user?.language == new.user?.language }
            .launchIn(lifecycleScope)

        settingsViewModel.languageUpdateRequired
            .onEach { required ->
                if (required) {
                    settingsViewModel.onLanguageUpdateHandled()
                    val intent = Intent(this@MainActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
            .launchIn(lifecycleScope)

        setContent {
            MusicAppTheme {
                val uiState by mainViewModel.uiState.collectAsState()

                if (uiState.isLoggedOut) {
                    LaunchedEffect(Unit) {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }

                if (!uiState.languageReady || uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    AppNavigation(
                        mainViewModel = mainViewModel,
                        settingsViewModel = settingsViewModel,
                        startRhythmGame = {
                            val intent = Intent(this, RhythmGameActivity::class.java)
                            startActivity(intent)
                        },
                        startStaffGame = {
                            val intent = Intent(this, StaffGameActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    // (El resto de tu MainActivity se mantiene igual)
    override fun onResume() {
        super.onResume()
        NotificationScheduler.cancelNotification(this)
    }

    override fun onStop() {
        super.onStop()
        if (!mainViewModel.uiState.value.isLoggedOut) {
            NotificationScheduler.scheduleNotification(this)
        }
    }
}