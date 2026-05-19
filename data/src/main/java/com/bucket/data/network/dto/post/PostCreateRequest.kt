package com.bucket.data.network.dto.post

import kotlinx.serialization.Serializable

@Serializable
data class PostCreateRequest(
    val goal: String,
    val categoryId: Long,
    val startDate: String?,
    val memo: String,
)
