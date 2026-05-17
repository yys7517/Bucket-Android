package com.bucket.data.network.dto.post

import kotlinx.serialization.Serializable

/** PATCH /posts/{postId}/small-goals/{smallGoalId} 요청 바디. */
@Serializable
data class SmallGoalUpdateRequest(
    val content: String,
    val color: String,
    val isCompleted: Boolean,
    val sortOrder: Int,
)
