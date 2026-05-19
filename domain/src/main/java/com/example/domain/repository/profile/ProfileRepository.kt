package com.example.domain.repository.profile

import com.example.domain.model.profile.Profile
import com.example.domain.model.profile.ProfilePostStatus
import com.example.domain.model.profile.ProfilePostType
import com.example.domain.model.home.PostCard

interface ProfileRepository {
    suspend fun fetchMyProfile(): Result<Profile>
    suspend fun fetchProfile(userId: Long): Result<Profile>
    suspend fun fetchProfilePosts(
        userId: Long,
        type: ProfilePostType,
        status: ProfilePostStatus,
    ): Result<List<PostCard>>
}
