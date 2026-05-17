package com.example.domain.model.post

data class Todo(
    val id: Long,
    val content: String,
    val color: String,
    val isComplete: Boolean,
    val position: Int = 0,
)
