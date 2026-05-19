package com.bucket.data.datasource.profile

import com.bucket.data.network.dto.common.BaseResponse
import com.bucket.data.network.dto.profile.ProfilePostsResponse
import com.bucket.data.network.dto.profile.ProfileResponse

interface ProfileDataSource {
    suspend fun getMyProfile(): BaseResponse<ProfileResponse>
    suspend fun getProfile(userId: Long): BaseResponse<ProfileResponse>
    suspend fun getProfilePosts(
        userId: Long,
        type: String,
        status: String,
    ): BaseResponse<ProfilePostsResponse>
}
