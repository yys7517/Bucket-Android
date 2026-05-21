package com.example.domain.repository.auth

import com.example.domain.model.user.UserInfo
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginWithKakao(accessToken: String): Result<UserInfo>
    suspend fun logout(): Result<Unit>
    suspend fun hasSavedLogin(): Boolean
    suspend fun getSavedUserId(): Long?
    fun observeSavedLogin(): Flow<Boolean>
}
