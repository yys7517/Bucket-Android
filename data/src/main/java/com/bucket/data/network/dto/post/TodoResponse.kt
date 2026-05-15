package com.bucket.data.network.dto.post

import kotlinx.serialization.Serializable

@Serializable
data class TodoResponse(
    val id: Long,
    val sortOrder: Int,
    val content: String,
    val color: String,
    val isComplete: Boolean = false,
)
