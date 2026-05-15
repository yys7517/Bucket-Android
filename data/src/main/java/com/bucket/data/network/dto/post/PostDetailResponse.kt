package com.bucket.data.network.dto.post

import com.bucket.data.network.dto.user.UserInfoResponse
import kotlinx.serialization.Serializable


@Serializable
data class PostDetailResponse(
    val id: Long,
    val category: String,
    val categoryColor: String,
    val title: String,
    val memo: String,
    val likeCount: Int,
    val startDate: String,
    val userInfo: UserInfoResponse,
    val plans: List<PostPlanDetailResponse>,
    val isLiked: Boolean,
    val isMine: Boolean
)
