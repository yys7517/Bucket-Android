package com.bucket.data.datasource.profile

import com.bucket.data.network.di.DefaultNetwork
import com.bucket.data.network.dto.common.BaseResponse
import com.bucket.data.network.dto.profile.ProfilePostsResponse
import com.bucket.data.network.dto.profile.ProfileResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
    @param:DefaultNetwork
    private val client: HttpClient,
) : ProfileDataSource {
    override suspend fun getMyProfile(): BaseResponse<ProfileResponse> =
        client.get("profile/me").body()

    override suspend fun getProfile(userId: Long): BaseResponse<ProfileResponse> =
        client.get("profile/$userId").body()

    override suspend fun getProfilePosts(
        userId: Long,
        type: String,
        status: String,
    ): BaseResponse<ProfilePostsResponse> =
        client.get("profile/$userId/posts") {
            parameter("type", type)
            parameter("status", status)
        }.body()
}
