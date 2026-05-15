package com.example.domain.repository.auth

import com.example.domain.model.user.UserInfo
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginWithKakao(accessToken: String): Result<UserInfo>
    suspend fun hasSavedLogin(): Boolean
    fun observeSavedLogin(): Flow<Boolean>
}
