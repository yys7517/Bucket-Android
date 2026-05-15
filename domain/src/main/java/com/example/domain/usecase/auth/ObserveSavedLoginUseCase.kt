package com.example.domain.usecase.auth

import com.example.domain.repository.auth.AuthRepository
import javax.inject.Inject

class ObserveSavedLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke() = authRepository.observeSavedLogin()
}
