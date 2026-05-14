package com.example.domain.usecase.auth

import com.example.domain.model.user.UserInfo
import com.example.domain.repository.auth.AuthRepository
import javax.inject.Inject

class LoginWithKakaoUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(accessToken: String): Result<UserInfo> =
        authRepository.loginWithKakao(accessToken)
}
