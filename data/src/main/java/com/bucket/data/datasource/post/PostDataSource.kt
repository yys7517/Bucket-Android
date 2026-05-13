package com.bucket.data.datasource.post

import com.bucket.data.network.dto.common.BaseResponse
import com.bucket.data.network.dto.post.PostDetailResponse

interface PostDataSource {
    suspend fun getPostDetail(postId: Long): BaseResponse<PostDetailResponse>
}
