package com.example.domain.model.home

import com.example.domain.model.user.Author

data class RecentBucket(
    val id: Long,
    val category: String,
    val categoryColor: String,
    val title: String,
    val author: Author,
    val startDate: String
)
