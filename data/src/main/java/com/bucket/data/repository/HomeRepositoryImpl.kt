package com.bucket.data.repository

import com.bucket.data.datasource.home.HomeDataSource
import com.bucket.data.mapper.asDomain
import com.bucket.data.mapper.asRecentBucket
import com.example.domain.model.home.PostCard
import com.example.domain.model.home.RecentBucket
import com.example.domain.repository.home.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeDataSource: HomeDataSource
): HomeRepository {
    override suspend fun fetchPopularBuckets(): Result<List<PostCard>> = runCatching {
        homeDataSource.getPopularBuckets()
            .data.map { it.asDomain() }
    }

    override suspend fun fetchRecentBuckets(): Result<List<RecentBucket>> = runCatching {
        homeDataSource.getRecentBuckets()
            .data.map { it.asRecentBucket() }
    }
}
