package com.bucket.data.datasource.category

import com.bucket.data.network.dto.category.CategoryResponse
import com.bucket.data.network.dto.common.BaseResponse

interface CategoryDataSource {
    suspend fun getCategories(): BaseResponse<List<CategoryResponse>>
}
