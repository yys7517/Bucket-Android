package com.bucket.data.repository

import com.bucket.data.datasource.auth.AuthDataSource
import com.example.domain.model.user.UserInfo
import com.example.domain.repository.auth.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource
) : AuthRepository {
    override suspend fun loginWithKakao(accessToken: String): Result<UserInfo> = runCatching {
        authDataSource.loginWithKakao(accessToken).data.asDomain()
    }
}
