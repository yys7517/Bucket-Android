package com.bucket.data.network.dto.post

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoResponse(
    val id: Long,
    val sortOrder: Int,
    val content: String,
    val color: String,
    @SerialName("isComplete") val isComplete: Boolean? = null,
    @SerialName("isCompleted") val isCompleted: Boolean? = null,
)
