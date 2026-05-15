package com.example.domain.model.home

import com.example.domain.model.user.Author

data class PopularBucket(
    val id: Long,
    val category: String,
    val categoryColor: String,
    val title: String,
    val author: Author,
    val likeCount: Int,
    val isLiked: Boolean,
    val progress: Int,
)
