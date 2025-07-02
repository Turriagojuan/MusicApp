package com.example.musicapp.data.model

data class User(
    val email: String = "",
    val username: String = "",
    val language: String = "es",
    val progress: Progress = Progress()
)