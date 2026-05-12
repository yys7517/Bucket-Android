package com.example.domain.repository.home

import com.example.domain.model.home.PopularBucket
import com.example.domain.model.home.RecentBucket

interface HomeRepository {
    suspend fun fetchPopularBuckets(): Result<List<PopularBucket>>
    suspend fun fetchRecentBuckets(): Result<List<RecentBucket>>
}