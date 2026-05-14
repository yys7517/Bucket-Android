package com.bucket.data.network.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val accessToken: String
)
