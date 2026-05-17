package com.bucket.data.network.dto.post

import kotlinx.serialization.Serializable

/** PATCH /posts/{postId} 응답의 data 필드. */
@Serializable
data class PostUpdateResponse(
    val id: Long,
    val goal: String,
    val memo: String,
    val startDate: String,
)
