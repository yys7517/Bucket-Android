package com.example.domain.model.user.request

data class AuthRequest(
    val email: String,
    val username: String,
    val password: String
)
