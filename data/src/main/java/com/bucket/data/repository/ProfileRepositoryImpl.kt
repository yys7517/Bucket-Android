package com.bucket.data.repository

import com.bucket.data.datasource.profile.ProfileDataSource
import com.bucket.data.mapper.asDomain
import com.example.domain.model.home.PostCard
import com.example.domain.model.profile.Profile
import com.example.domain.model.profile.ProfilePostStatus
import com.example.domain.model.profile.ProfilePostType
import com.example.domain.repository.profile.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileDataSource: ProfileDataSource
) : ProfileRepository {
    override suspend fun fetchMyProfile(): Result<Profile> = runCatching {
        profileDataSource.getMyProfile().data.asDomain()
    }

    override suspend fun fetchProfile(userId: Long): Result<Profile> = runCatching {
        profileDataSource.getProfile(userId).data.asDomain()
    }

    override suspend fun fetchProfilePosts(
        userId: Long,
        type: ProfilePostType,
        status: ProfilePostStatus,
    ): Result<List<PostCard>> = runCatching {
        profileDataSource.getProfilePosts(
            userId = userId,
            type = type.value,
            status = status.value,
        ).data.let { response ->
            response.posts.map { it.copy(status = response.status).asDomain() }
        }
    }
}
