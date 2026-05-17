package com.bucket.data.datasource.post

import com.bucket.data.network.dto.common.BaseResponse
import com.bucket.data.network.dto.post.LikeResponse
import com.bucket.data.network.dto.post.PostDetailResponse
import com.bucket.data.network.dto.post.PostUpdateRequest
import com.bucket.data.network.dto.post.PostUpdateResponse
import com.bucket.data.network.dto.post.SmallGoalCreateRequest
import com.bucket.data.network.dto.post.SmallGoalResponse
import com.bucket.data.network.dto.post.SmallGoalUpdateRequest

interface PostDataSource {
    suspend fun getPostDetail(postId: Long): BaseResponse<PostDetailResponse>
    suspend fun toggleLike(postId: Long): BaseResponse<LikeResponse>
    suspend fun updatePost(
        postId: Long,
        request: PostUpdateRequest,
    ): BaseResponse<PostUpdateResponse>
    suspend fun deletePost(postId: Long): BaseResponse<String?>

    suspend fun createSmallGoal(
        postId: Long,
        request: SmallGoalCreateRequest,
    ): BaseResponse<SmallGoalResponse>

    suspend fun updateSmallGoal(
        postId: Long,
        smallGoalId: Long,
        request: SmallGoalUpdateRequest,
    ): BaseResponse<SmallGoalResponse>

    suspend fun deleteSmallGoal(
        postId: Long,
        smallGoalId: Long,
    ): BaseResponse<String?>
}
