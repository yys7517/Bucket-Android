package com.example.domain.model.profile

data class Profile(
    val id: Long,
    val email: String,
    val username: String,
    val introduction: String,
    val profileImgUrl: String,
    val postCount: Int,
    val completedPostCount: Int,
    val receivedLikeCount: Int,
)
