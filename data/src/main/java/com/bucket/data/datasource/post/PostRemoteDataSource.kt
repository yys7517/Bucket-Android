package com.bucket.data.datasource.post

import com.bucket.data.network.di.DefaultNetwork
import com.bucket.data.network.dto.common.BaseResponse
import com.bucket.data.network.dto.post.PostDetailResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class PostRemoteDataSource @Inject constructor(
    @param:DefaultNetwork
    private val client: HttpClient,
): PostDataSource{
    override suspend fun getPostDetail(postId: Long): BaseResponse<PostDetailResponse> =
        client.get("posts/${postId}").body()

}