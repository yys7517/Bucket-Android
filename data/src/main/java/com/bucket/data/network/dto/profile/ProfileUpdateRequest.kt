package com.bucket.data.network.dto.profile

import kotlinx.serialization.Serializable

@Serializable
data class ProfileUpdateRequest(
    val username: String,
    val email: String,
    val introduction: String,
)
