package com.example.domain.model.home

data class PopularBucket(
    val id: Long,
    val category: String,
    val categoryColor: String,
    val title: String,
    val userName: String,
    val profileImageUrl: String,
    val likeCount: Int,
    val isLiked: Boolean,
    val progress: Int,
)
