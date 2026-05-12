package com.bucket.data.repository

import com.bucket.data.datasource.home.HomeDataSource
import com.bucket.data.mapper.asPopularBucket
import com.bucket.data.mapper.asRecentBucket
import com.example.domain.model.home.PopularBucket
import com.example.domain.model.home.RecentBucket
import com.example.domain.repository.home.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeDataSource: HomeDataSource
): HomeRepository {
    override suspend fun fetchPopularBuckets(): Result<List<PopularBucket>> = runCatching {
        homeDataSource.getPopularBuckets()
            .data.map { it.asPopularBucket() }
    }

    override suspend fun fetchRecentBuckets(): Result<List<RecentBucket>> = runCatching {
        homeDataSource.getRecentBuckets()
            .data.map { it.asRecentBucket() }
    }
}
