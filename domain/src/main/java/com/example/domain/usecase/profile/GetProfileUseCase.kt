package com.example.domain.usecase.profile

import com.example.domain.repository.profile.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(userId: Long) = profileRepository.fetchProfile(userId)
}
