package com.bucket.data.network.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    val id: Long,
    val email: String,
    val username: String,
    val profileImgUrl: String
)

/*
"userInfo": {
        "id": 1,
        "email": "test@example.com",
        "username": "test-user",
        "profileImgUrl": "https://example.com/test-user.png"
    },
 */
