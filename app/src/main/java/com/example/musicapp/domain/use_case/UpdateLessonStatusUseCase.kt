package com.example.musicapp.domain.use_case

import com.example.musicapp.data.repository.UserRepository
import com.example.musicapp.data.repository.UserRepositoryImpl
import com.example.musicapp.data.source.FirebaseService

class UpdateLessonStatusUseCase(
    private val userRepository: UserRepository = UserRepositoryImpl(FirebaseService())
) {
    suspend operator fun invoke(lessonId: String, completed: Boolean): Boolean {
        return userRepository.updateLessonCompletionStatus(lessonId, completed)
    }
}