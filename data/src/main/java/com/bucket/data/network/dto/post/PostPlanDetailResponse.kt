package com.bucket.data.network.dto.post

import kotlinx.serialization.Serializable

@Serializable
data class PostPlanDetailResponse(
    val id: Long,
    val sortOrder: Int,
    val content: String,
    val isComplete: Boolean,
)

/*
{
    "id": 1,
    "sortOrder": 1,
    "content": "항공권 예약하기",
    "isComplete": true
},
*/
