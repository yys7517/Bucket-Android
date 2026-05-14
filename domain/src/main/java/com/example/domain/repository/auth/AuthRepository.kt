package com.example.domain.repository.auth

import com.example.domain.model.user.UserInfo

interface AuthRepository {
    suspend fun loginWithKakao(accessToken: String): Result<UserInfo>
}
