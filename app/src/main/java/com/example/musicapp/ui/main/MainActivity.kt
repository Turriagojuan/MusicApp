package com.example.musicapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.os.LocaleListCompat
import com.example.musicapp.notifications.NotificationScheduler
import com.example.musicapp.ui.login.LoginActivity
import com.example.musicapp.ui.navigation.AppNavigation
import com.example.musicapp.ui.rhythm_module.RhythmGameActivity
import com.example.musicapp.ui.staff_module.StaffGameActivity
import com.example.musicapp.ui.theme.MusicAppTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MusicAppTheme {
                val uiState by mainViewModel.uiState.collectAsState()
                val languageUpdateRequired by settingsViewModel.languageUpdateRequired.collectAsState()

                // --- LÓGICA CORREGIDA ---
                // Se observa el estado del usuario. Cuando el usuario se carga (deja de ser null),
                // se aplica la configuración de idioma.
                val currentUser = uiState.user
                if (currentUser != null) {
                    LaunchedEffect(currentUser.language) {
                        val appLocale = LocaleListCompat.forLanguageTags(currentUser.language)
                        AppCompatDelegate.setApplicationLocales(appLocale)
                    }
                }

                // Si el usuario cierra sesión, vuelve a la pantalla de Login.
                if (uiState.isLoggedOut) {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                // Si se cambia el idioma desde Ajustes, reinicia la actividad para aplicar los cambios.
                if (languageUpdateRequired) {
                    // Usamos un LaunchedEffect para que solo se ejecute una vez por cambio.
                    LaunchedEffect(Unit) {
                        settingsViewModel.onLanguageUpdateHandled() // Resetea el flag
                        this@MainActivity.recreate() // Reinicia la actividad
                    }
                }

                // Lanza la navegación principal de la aplicación.
                AppNavigation(
                    mainViewModel = mainViewModel,
                    settingsViewModel = settingsViewModel, // <- CAMBIO: Se pasa la instancia
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

    override fun onResume() {
        super.onResume()
        NotificationScheduler.cancelNotification(this)
    }

    override fun onStop() {
        super.onStop()
        // Solo programa la notificación si el usuario no ha cerrado sesión
        if (!mainViewModel.uiState.value.isLoggedOut) {
            NotificationScheduler.scheduleNotification(this)
        }
    }
}