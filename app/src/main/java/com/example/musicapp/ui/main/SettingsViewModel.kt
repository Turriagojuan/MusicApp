package com.example.musicapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.repository.UserRepositoryImpl
import com.example.musicapp.data.source.FirebaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    private val userRepository = UserRepositoryImpl(FirebaseService())

    val languageUpdateRequired = MutableStateFlow(false)

    fun updateUserLanguage(languageCode: String) {
        viewModelScope.launch {
            if (userRepository.updateUserLanguage(languageCode)) {
                languageUpdateRequired.value = true
            }
        }
    }

    fun onLanguageUpdateHandled() {
        languageUpdateRequired.value = false
    }
}