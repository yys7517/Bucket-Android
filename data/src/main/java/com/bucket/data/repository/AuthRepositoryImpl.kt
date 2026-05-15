package com.bucket.data.repository

import com.bucket.data.datasource.auth.AuthDataSource
import com.bucket.data.datasource.auth.AuthLocalDataSource
import com.example.domain.model.user.UserInfo
import com.example.domain.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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

    override suspend fun hasSavedLogin(): Boolean {
        val userId = authLocalDataSource.userId.first()
        val accessToken = authLocalDataSource.accessToken.first()
        val refreshToken = authLocalDataSource.refreshToken.first()

        return userId != null &&
            accessToken.isNullOrBlank().not() &&
            refreshToken.isNullOrBlank().not()
    }

    override fun observeSavedLogin(): Flow<Boolean> =
        combine(
            authLocalDataSource.userId,
            authLocalDataSource.accessToken,
            authLocalDataSource.refreshToken
        ) { userId, accessToken, refreshToken ->
            userId != null &&
                accessToken.isNullOrBlank().not() &&
                refreshToken.isNullOrBlank().not()
        }.distinctUntilChanged()
}
