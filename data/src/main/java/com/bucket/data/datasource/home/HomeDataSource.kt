package com.bucket.data.datasource.home

import com.bucket.data.network.dto.common.BaseResponse
import com.bucket.data.network.dto.home.BucketCardResponse

interface HomeDataSource {
    suspend fun getPopularBuckets(): BaseResponse<List<BucketCardResponse>>
    suspend fun getRecentBuckets(): BaseResponse<List<BucketCardResponse>>
}
