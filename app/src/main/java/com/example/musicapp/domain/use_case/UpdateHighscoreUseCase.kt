package com.example.musicapp.domain.use_case

import com.example.musicapp.data.repository.UserRepository
import com.example.musicapp.data.repository.UserRepositoryImpl
import com.example.musicapp.data.source.FirebaseService

class UpdateHighscoreUseCase(
    private val userRepository: UserRepository = UserRepositoryImpl(FirebaseService())
) {
    suspend operator fun invoke(module: String, score: Int): Boolean {
        return userRepository.updateHighscore(module, score)
    }
}