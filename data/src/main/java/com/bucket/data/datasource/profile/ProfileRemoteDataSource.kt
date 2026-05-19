package com.bucket.data.datasource.profile

import com.bucket.data.network.di.DefaultNetwork
import com.bucket.data.network.dto.common.BaseResponse
import com.bucket.data.network.dto.profile.ProfilePostsResponse
import com.bucket.data.network.dto.profile.ProfileResponse
import com.bucket.data.network.dto.profile.ProfileUpdateRequest
import com.bucket.data.network.dto.profile.ProfileUpdateResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class ProfileRemoteDataSource @Inject constructor(
    @param:DefaultNetwork
    private val client: HttpClient,
) : ProfileDataSource {
    override suspend fun getMyProfile(): BaseResponse<ProfileResponse> =
        client.get("profile/me").body()

    override suspend fun getProfile(userId: Long): BaseResponse<ProfileResponse> =
        client.get("profile/$userId").body()

    override suspend fun updateMyProfile(
        request: ProfileUpdateRequest
    ): BaseResponse<ProfileUpdateResponse> =
        client.patch("profile/me") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

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
