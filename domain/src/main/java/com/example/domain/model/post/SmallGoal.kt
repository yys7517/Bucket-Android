package com.example.domain.model.post

data class SmallGoal(
    val id: Long,
    val sortOrder: Int,
    val content: String,
    val isComplete: Boolean,
    val color: String = "#8D6BE8",
    val todos: List<Todo> = emptyList(),
)
