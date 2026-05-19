package com.bucket.data.datasource.home

import com.bucket.data.network.dto.common.BaseResponse
import com.bucket.data.network.dto.home.PostCardResponse

interface HomeDataSource {
    suspend fun getPopularBuckets(): BaseResponse<List<PostCardResponse>>
    suspend fun getRecentBuckets(): BaseResponse<List<PostCardResponse>>
}
