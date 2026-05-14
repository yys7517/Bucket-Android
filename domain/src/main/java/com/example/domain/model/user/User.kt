package com.example.domain.model.user

data class UserInfo(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String
)