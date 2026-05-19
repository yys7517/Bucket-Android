package com.bucket.data.network.dto.profile

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val id: Long,
    val email: String,
    val username: String,
    val introduction: String = "",
    val bio: String = "",
    val profileImgUrl: String = "",
    val postCount: Int,
    val completedPostCount: Int,
    val receivedLikeCount: Int,
)
