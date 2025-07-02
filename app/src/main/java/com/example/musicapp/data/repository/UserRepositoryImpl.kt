package com.example.musicapp.data.repository

import com.example.musicapp.data.model.User
import com.example.musicapp.data.source.FirebaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(private val firebaseService: FirebaseService) : UserRepository {

    // ... (login, signUp, logout, etc. sin cambios)
    override suspend fun login(email: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val userId = firebaseService.login(email, password)
                firebaseService.getUserData(userId)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun signUp(user: User, password: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val userId = firebaseService.signUp(user, password)
                firebaseService.getUserData(userId)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override fun logout() {
        firebaseService.logout()
    }

    override suspend fun getCurrentUser(): User? {
        return withContext(Dispatchers.IO) {
            try {
                firebaseService.getCurrentUser()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun updateLessonCompletionStatus(lessonId: String, completed: Boolean): Boolean {
        val fieldPath = when (lessonId) {
            "1.1" -> "progress.rhythmModule.lesson_1_1_completed"
            "1.2" -> "progress.rhythmModule.lesson_1_2_completed"
            "1.3" -> "progress.rhythmModule.lesson_1_3_completed"
            "2.1" -> "progress.staffModule.lesson_2_1_completed"
            "2.2" -> "progress.staffModule.lesson_2_2_completed"
            "2.3" -> "progress.staffModule.lesson_2_3_completed"
            "2.4" -> "progress.staffModule.lesson_2_4_completed"
            else -> return false
        }

        return withContext(Dispatchers.IO) {
            firebaseService.updateUserField(fieldPath, completed)
        }
    }

    override suspend fun updateUserLanguage(languageCode: String): Boolean {
        return withContext(Dispatchers.IO) {
            firebaseService.updateUserField("language", languageCode)
        }
    }

    // NUEVA IMPLEMENTACIÓN
    override suspend fun updateHighscore(module: String, score: Int): Boolean {
        return withContext(Dispatchers.IO) {
            val currentUser = getCurrentUser() ?: return@withContext false

            val (fieldPath, currentHighscore) = when (module) {
                "rhythm" -> "progress.rhythmModule.game_highscore" to currentUser.progress.rhythmModule.game_highscore
                "staff" -> "progress.staffModule.game_highscore" to currentUser.progress.staffModule.game_highscore
                else -> return@withContext false
            }

            // Solo actualiza si la nueva puntuación es mayor
            if (score > currentHighscore) {
                firebaseService.updateUserField(fieldPath, score)
            } else {
                true // Se considera exitoso aunque no se actualice
            }
        }
    }
}