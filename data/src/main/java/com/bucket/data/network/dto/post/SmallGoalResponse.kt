package com.bucket.data.network.dto.post

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SmallGoalResponse(
    val id: Long? = null,
    val sortOrder: Int,
    val content: String,
    @SerialName("isComplete") val isComplete: Boolean? = null,
    @SerialName("isCompleted") val isCompleted: Boolean? = null,
    val color: String = "#8D6BE8",
    val todos: List<TodoResponse> = emptyList(),
)

/*
{
    "id": 1,
    "sortOrder": 1,
    "content": "항공권 예약하기",
    "isComplete": true
},
*/
