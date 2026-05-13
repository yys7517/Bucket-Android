package com.bucket.data.datasource.category

import com.bucket.data.network.di.DefaultNetwork
import com.bucket.data.network.dto.category.CategoryResponse
import com.bucket.data.network.dto.common.BaseResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

class CategoryRemoteDataSource @Inject constructor(
    @param:DefaultNetwork
    private val client: HttpClient
) : CategoryDataSource {
    override suspend fun getCategories(): BaseResponse<List<CategoryResponse>> =
        client.get("category").body()
}
