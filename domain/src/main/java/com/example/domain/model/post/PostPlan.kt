package com.example.domain.model.post

data class PostPlan(
    val id: Long,
    val sortOrder: Int,
    val content: String,
    val isComplete: Boolean,
)
