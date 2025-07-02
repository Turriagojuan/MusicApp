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

        mainViewModel.uiState.value.user?.language?.let { lang ->
            if (lang.isNotEmpty()) {
                val appLocale = LocaleListCompat.forLanguageTags(lang)
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
        }

        setContent {
            MusicAppTheme {
                val uiState by mainViewModel.uiState.collectAsState()
                val languageUpdateRequired by settingsViewModel.languageUpdateRequired.collectAsState()

                if (uiState.isLoggedOut) {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                if (languageUpdateRequired) {
                    LaunchedEffect(Unit) {
                        settingsViewModel.onLanguageUpdateHandled()
                        this@MainActivity.recreate()
                    }
                }

                AppNavigation(
                    mainViewModel = mainViewModel,
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
        NotificationScheduler.scheduleNotification(this)
    }
}