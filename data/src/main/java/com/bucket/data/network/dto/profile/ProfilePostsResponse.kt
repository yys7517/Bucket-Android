package com.bucket.data.network.dto.profile

import com.bucket.data.network.dto.home.PostCardResponse
import kotlinx.serialization.Serializable

@Serializable
data class ProfilePostsResponse(
    val type: String,
    val status: String,
    val posts: List<PostCardResponse> = emptyList(),
)
