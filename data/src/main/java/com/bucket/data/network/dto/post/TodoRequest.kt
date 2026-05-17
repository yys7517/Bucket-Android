package com.bucket.data.network.dto.post

import kotlinx.serialization.Serializable

@Serializable
data class TodoRequest(
    val content: String,
    val color: String,
    val isCompleted: Boolean,
    val sortOrder: Int,
)
