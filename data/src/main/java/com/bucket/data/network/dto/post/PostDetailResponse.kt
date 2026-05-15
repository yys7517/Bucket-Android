package com.bucket.data.network.dto.post

import kotlinx.serialization.Serializable


@Serializable
data class PostDetailResponse(
    val id: Long,
    val category: String,
    val categoryColor: String,
    val goal: String = "",
    val memo: String,
    val likeCount: Int,
    val startDate: String,
    val smallGoals: List<SmallGoalResponse> = emptyList(),
    val isLiked: Boolean,
    val isMine: Boolean
)
