package com.bucket.data.network.dto.home

import kotlinx.serialization.Serializable

@Serializable
data class GoalSummaryResponse(
    val totalCount: Int,
    val completedCount: Int,
    val progressRate: Int,
)

/*
"planSummary": {
    "totalCount": 2,
    "completedCount": 1,
    "progressRate": 50
}*/
