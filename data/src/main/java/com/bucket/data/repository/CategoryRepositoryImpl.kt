package com.bucket.data.repository

import com.bucket.data.datasource.category.CategoryDataSource
import com.bucket.data.mapper.asBucketCategory
import com.example.domain.model.category.BucketCategory
import com.example.domain.repository.category.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDataSource: CategoryDataSource
) : CategoryRepository {
    override suspend fun fetchCategories(): List<BucketCategory> =
        categoryDataSource.getCategories()
            .data
            .map { it.asBucketCategory() }

}
