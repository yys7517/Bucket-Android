package com.example.domain.repository.category

import com.example.domain.model.category.BucketCategory

interface CategoryRepository {
    suspend fun fetchCategories(): List<BucketCategory>
}
