package com.example.domain.usecase.profile

import com.example.domain.repository.profile.ProfileRepository
import javax.inject.Inject

class GetMyProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke() = profileRepository.fetchMyProfile()
}
