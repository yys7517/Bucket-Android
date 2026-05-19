package com.bucket.data.datasource.post

import com.bucket.data.network.dto.common.BaseResponse
import com.bucket.data.network.dto.post.BookmarkResponse
import com.bucket.data.network.dto.post.LikeResponse
import com.bucket.data.network.dto.post.PostCreateRequest
import com.bucket.data.network.dto.post.PostDetailResponse
import com.bucket.data.network.dto.post.PostUpdateRequest
import com.bucket.data.network.dto.post.PostUpdateResponse
import com.bucket.data.network.dto.post.SmallGoalCreateRequest
import com.bucket.data.network.dto.post.SmallGoalResponse
import com.bucket.data.network.dto.post.SmallGoalUpdateRequest
import com.bucket.data.network.dto.post.TodoRequest
import com.bucket.data.network.dto.post.TodoResponse

interface PostDataSource {
    suspend fun createPost(request: PostCreateRequest): BaseResponse<PostDetailResponse>
    suspend fun getPostDetail(postId: Long): BaseResponse<PostDetailResponse>
    suspend fun toggleLike(postId: Long): BaseResponse<LikeResponse>
    suspend fun toggleBookmark(postId: Long): BaseResponse<BookmarkResponse>
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

    suspend fun createTodo(
        postId: Long,
        smallGoalId: Long,
        request: TodoRequest,
    ): BaseResponse<TodoResponse>

    suspend fun updateTodo(
        postId: Long,
        smallGoalId: Long,
        todoId: Long,
        request: TodoRequest,
    ): BaseResponse<TodoResponse>

    suspend fun deleteTodo(
        postId: Long,
        smallGoalId: Long,
        todoId: Long,
    ): BaseResponse<String?>
}
