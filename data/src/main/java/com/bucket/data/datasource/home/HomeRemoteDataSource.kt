package com.bucket.data.datasource.home

import com.bucket.data.network.di.DefaultNetwork
import com.bucket.data.network.dto.common.BaseResponse
import com.bucket.data.network.dto.home.BucketCardResponse
import io.ktor.client.call.body
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import javax.inject.Inject

class HomeRemoteDataSource @Inject constructor(
    @param:DefaultNetwork
    private val client: HttpClient,
): HomeDataSource {
    override suspend fun getPopularBuckets(): BaseResponse<List<BucketCardResponse>> =
        client.get("home/popular").body()

    override suspend fun getRecentBuckets(): BaseResponse<List<BucketCardResponse>> =
        client.get("home/recent").body()
}
