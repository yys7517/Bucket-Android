package com.bucket.data.network.dto.auth

import com.example.domain.model.user.UserInfo
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String
) {
    fun asDomain()= UserInfo(userId, accessToken, refreshToken)
}

/*
    {
        "userId": 3,
        "accessToken": "...",
        "refreshToken": "..."
    }
 */
