package com.example.domain.model.post

data class BucketPostDetail(
    val id: Long,
    val title: String,
    val memo: String,
    val category: String,
    val categoryColor: String,
    val likeCount: Int,
    val startDate: String,
    val userId: Long,
    val username: String,
    val profileImage: String,
    val plans: List<PostPlan>,
    val isLiked: Boolean,
    val isMine: Boolean,
)