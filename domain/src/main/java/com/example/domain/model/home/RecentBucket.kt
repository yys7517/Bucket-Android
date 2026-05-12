package com.example.domain.model.home

data class RecentBucket(
    val id: Long,
    val category: String,
    val categoryColor: String,
    val title: String,
    val userName: String,
    val profileImageUrl: String,
    val startDate: String
)
