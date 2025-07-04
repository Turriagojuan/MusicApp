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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- INICIO DE LA LÓGICA DE IDIOMA CORREGIDA ---

        // 1. Se observa el idioma del usuario desde el ViewModel.
        //    Esto se hace fuera de setContent para aplicar el idioma antes de que se dibuje la UI.
        mainViewModel.uiState
            .onEach { state ->
                // Cuando se obtiene el usuario, se extrae su idioma guardado.
                state.user?.language?.let { lang ->
                    val appLocale = LocaleListCompat.forLanguageTags(lang)
                    // Se aplica el idioma a toda la aplicación.
                    // Se comprueba si el idioma ya es el correcto para evitar un bucle de actualizaciones.
                    if (AppCompatDelegate.getApplicationLocales() != appLocale) {
                        AppCompatDelegate.setApplicationLocales(appLocale)
                    }
                }
            }
            .distinctUntilChanged { old, new -> old.user?.language == new.user?.language }
            .launchIn(lifecycleScope)

        // 2. Se observa si el usuario ha solicitado un cambio de idioma desde la pantalla de Ajustes.
        settingsViewModel.languageUpdateRequired
            .onEach { required ->
                if (required) {
                    settingsViewModel.onLanguageUpdateHandled() // Se resetea el flag para evitar reinicios múltiples.
                    // **CORRECCIÓN: Se reemplaza recreate() por un reinicio manual de la actividad**
                    val intent = Intent(this@MainActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
            .launchIn(lifecycleScope)

        // --- FIN DE LA LÓGICA DE IDIOMA CORREGIDA ---

        setContent {
            MusicAppTheme {
                val uiState by mainViewModel.uiState.collectAsState()

                // Si el usuario cierra sesión, se le redirige a la pantalla de Login.
                if (uiState.isLoggedOut) {
                    // Se usa LaunchedEffect para realizar la navegación fuera del hilo de la UI.
                    LaunchedEffect(Unit) {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }

                // Se lanza la navegación principal de la aplicación.
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

    override fun onResume() {
        super.onResume()
        // Cancela cualquier notificación pendiente cuando el usuario vuelve a la app.
        NotificationScheduler.cancelNotification(this)
    }

    override fun onStop() {
        super.onStop()
        // Programa una notificación de recordatorio si el usuario no ha cerrado sesión.
        if (!mainViewModel.uiState.value.isLoggedOut) {
            NotificationScheduler.scheduleNotification(this)
        }
    }
}