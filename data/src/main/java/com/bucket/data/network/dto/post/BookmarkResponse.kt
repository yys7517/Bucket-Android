package com.bucket.data.network.dto.post

import kotlinx.serialization.Serializable

@Serializable
data class BookmarkResponse(
    val userId: Long,
    val postId: Long,
    val isBookmarked: Boolean,
)
