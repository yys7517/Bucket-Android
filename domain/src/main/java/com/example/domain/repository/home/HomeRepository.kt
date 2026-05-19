package com.example.domain.repository.home

import com.example.domain.model.home.PostCard
import com.example.domain.model.home.RecentBucket

interface HomeRepository {
    suspend fun fetchPopularBuckets(): Result<List<PostCard>>
    suspend fun fetchRecentBuckets(): Result<List<RecentBucket>>
}