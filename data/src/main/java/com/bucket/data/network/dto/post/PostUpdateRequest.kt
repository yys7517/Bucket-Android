package com.bucket.data.network.dto.post

import kotlinx.serialization.Serializable

/**
 * PATCH /posts/{postId} 요청 바디.
 *
 * 모든 필드 보내는 형태 ({ goal, memo, startDate }).
 */
@Serializable
data class PostUpdateRequest(
    val goal: String,
    val memo: String,
    val startDate: String?,
)
