package com.example.musicapp.data.repository

import com.example.musicapp.data.model.User

interface UserRepository {
    suspend fun login(email: String, password: String): User?
    suspend fun signUp(user: User, password: String): User?
    fun logout()
    suspend fun getCurrentUser(): User?
    suspend fun updateLessonCompletionStatus(lessonId: String, completed: Boolean): Boolean
    suspend fun updateUserLanguage(languageCode: String): Boolean

    // NUEVO MÉTODO
    suspend fun updateHighscore(module: String, score: Int): Boolean
}