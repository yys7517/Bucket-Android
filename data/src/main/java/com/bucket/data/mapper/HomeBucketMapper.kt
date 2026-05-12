package com.bucket.data.mapper

import com.bucket.data.network.dto.home.BucketCardResponse
import com.example.domain.model.home.PopularBucket
import com.example.domain.model.home.RecentBucket

fun BucketCardResponse.asPopularBucket(): PopularBucket =
    PopularBucket(
        id = this.id,
        category = this.category,
        categoryColor = this.categoryColor,
        title = this.title,
        userName = this.userInfo.username,
        profileImageUrl = this.userInfo.profileImgUrl,
        likeCount = this.likeCount,
        isLiked = this.isLiked,
        progress = this.planSummary.progressRate
    )

fun BucketCardResponse.asRecentBucket(): RecentBucket =
    RecentBucket(
        id = this.id,
        category = this.category,
        categoryColor = this.categoryColor,
        title = this.title,
        userName = this.userInfo.username,
        profileImageUrl = this.userInfo.profileImgUrl,
        startDate = this.startDate,
    )
