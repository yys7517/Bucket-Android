package com.bucket.data.network.dto.home

import com.bucket.data.network.dto.user.UserInfoResponse
import kotlinx.serialization.Serializable

@Serializable
data class PostCardResponse(
    val id: Long,
    val category: String,
    val categoryColor: String = "",
    val goal: String = "",
    val likeCount: Int,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val startDate: String? = null,
    val userInfo: UserInfoResponse,
    val smallGoalSummary: GoalSummaryResponse,
    val smallGoals: Map<Int, SmallGoalSummaryResponse> = emptyMap(),
    val status: String = "",
)
