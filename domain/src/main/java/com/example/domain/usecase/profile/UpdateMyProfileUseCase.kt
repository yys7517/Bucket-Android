package com.example.domain.usecase.profile

import com.example.domain.repository.profile.ProfileRepository
import javax.inject.Inject

class UpdateMyProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        introduction: String,
    ) = profileRepository.updateMyProfile(username, email, introduction)
}
