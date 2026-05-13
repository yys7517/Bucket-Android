package com.bucket.data.mapper

import com.bucket.data.network.dto.category.CategoryResponse
import com.example.domain.model.category.BucketCategory

fun CategoryResponse.asBucketCategory(): BucketCategory =
    BucketCategory(
        id = id,
        name = name,
        bucketCount = count,
        categoryColor = color
    )
