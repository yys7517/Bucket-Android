package com.bucket.data.network.dto.post

import kotlinx.serialization.Serializable

/** POST /posts/{postId}/small-goals 요청 바디. */
@Serializable
data class SmallGoalCreateRequest(
    val content: String,
    val color: String,
    val isCompleted: Boolean,
    val sortOrder: Int,
)
