package com.example.domain.model.post

import com.example.domain.model.user.Author

data class PostDetail(
    val id: Long,
    val title: String,
    val memo: String,
    val category: String,
    val categoryColor: String,
    val likeCount: Int,
    val startDate: String,
    val author: Author,
    val smallGoals: List<SmallGoal>,
    val isLiked: Boolean,
    val isMine: Boolean,
    val isBookmarked: Boolean = false,
)
