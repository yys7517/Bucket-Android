package com.bucket.data.mapper

import com.bucket.data.network.dto.home.PostCardResponse
import com.example.domain.model.home.PostCard
import com.example.domain.model.home.RecentBucket
import com.example.domain.model.home.SmallGoalSummary
import com.example.domain.model.user.Author

fun PostCardResponse.asDomain(): PostCard =
    PostCard(
        id = this.id,
        category = this.category,
        categoryColor = this.categoryColor,
        title = this.goal,
        author = Author(
            userId = this.userInfo.id,
            username = this.userInfo.username,
            profileImgUrl = this.userInfo.profileImgUrl
        ),
        likeCount = this.likeCount,
        isLiked = this.isLiked,
        startDate = this.startDate.orEmpty(),
        completedCount = this.smallGoalSummary.completedCount,
        totalCount = this.smallGoalSummary.totalCount,
        progressRate = this.smallGoalSummary.progressRate,
        smallGoals = this.smallGoals.mapValues { (_, v) ->
            SmallGoalSummary(content = v.content, color = v.color, isCompleted = v.isCompleted)
        },
        isBookmarked = this.isBookmarked,
        status = this.status,
    )

fun PostCardResponse.asRecentBucket(): RecentBucket =
    RecentBucket(
        id = this.id,
        category = this.category,
        categoryColor = this.categoryColor,
        title = this.goal,
        author = Author(
            userId = this.userInfo.id,
            username = this.userInfo.username,
            profileImgUrl = this.userInfo.profileImgUrl
        ),
        startDate = this.startDate.orEmpty(),
        likeCount = this.likeCount,
        isLiked = this.isLiked,
        completedCount = this.smallGoalSummary.completedCount,
        totalCount = this.smallGoalSummary.totalCount,
        progressRate = this.smallGoalSummary.progressRate,
        isBookmarked = this.isBookmarked,
    )
