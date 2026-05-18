package com.bucket.data.network.dto.home

import kotlinx.serialization.Serializable

@Serializable
data class SmallGoalSummaryResponse(
    val content: String,
    val color: String,
    val isCompleted: Boolean,
)
