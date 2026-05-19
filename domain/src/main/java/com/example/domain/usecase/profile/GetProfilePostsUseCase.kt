package com.example.domain.usecase.profile

import com.example.domain.model.profile.ProfilePostStatus
import com.example.domain.model.profile.ProfilePostType
import com.example.domain.repository.profile.ProfileRepository
import javax.inject.Inject

class GetProfilePostsUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(
        userId: Long,
        type: ProfilePostType,
        status: ProfilePostStatus,
    ) = profileRepository.fetchProfilePosts(userId, type, status)
}
