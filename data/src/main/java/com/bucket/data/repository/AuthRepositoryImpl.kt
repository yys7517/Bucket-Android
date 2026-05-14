package com.bucket.data.repository

import com.bucket.data.datasource.auth.AuthDataSource
import com.bucket.data.datasource.auth.AuthLocalDataSource
import com.example.domain.model.user.UserInfo
import com.example.domain.repository.auth.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val authLocalDataSource: AuthLocalDataSource
) : AuthRepository {
    override suspend fun loginWithKakao(accessToken: String): Result<UserInfo> = runCatching {
        authDataSource.loginWithKakao(accessToken)
            .data
            .asDomain()
            .also { userInfo ->
                authLocalDataSource.saveUserInfo(userInfo)
            }
    }
}
