package com.example.domain.usecase.auth

import com.example.domain.repository.auth.AuthRepository
import javax.inject.Inject

class CheckAutoLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Boolean = authRepository.hasSavedLogin()
}
