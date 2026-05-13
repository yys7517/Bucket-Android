package com.bucket.data.network.dto.category

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val id: Long = 0L,
    val name: String = "",
    val count: Int = 0,
    val color: String = ""
)
