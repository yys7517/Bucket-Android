package com.bucket.data.network.dto.post

import kotlinx.serialization.Serializable

@Serializable
data class LikeResponse(
    val isLiked: Boolean,
    val likeCount: Int
)
