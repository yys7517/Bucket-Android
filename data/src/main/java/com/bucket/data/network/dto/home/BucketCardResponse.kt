package com.bucket.data.network.dto.home

import com.bucket.data.network.dto.user.UserInfoResponse
import kotlinx.serialization.Serializable

@Serializable
data class BucketCardResponse(
    val id: Long,
    val category: String,
    val categoryColor: String = "",
    val goal: String = "",
    val likeCount: Int,
    val isLiked: Boolean = false,
    val startDate: String,
    val userInfo: UserInfoResponse,
    val smallGoalSummary: GoalSummaryResponse,
    val smallGoals: Map<Int, SmallGoalSummaryResponse> = emptyMap(),
)
