package com.bucket.data.datasource.post

import com.bucket.data.network.di.DefaultNetwork
import com.bucket.data.network.dto.common.BaseResponse
import com.bucket.data.network.dto.post.LikeResponse
import com.bucket.data.network.dto.post.PostDetailResponse
import com.bucket.data.network.dto.post.PostUpdateRequest
import com.bucket.data.network.dto.post.PostUpdateResponse
import com.bucket.data.network.dto.post.SmallGoalCreateRequest
import com.bucket.data.network.dto.post.SmallGoalResponse
import com.bucket.data.network.dto.post.SmallGoalUpdateRequest
import com.bucket.data.network.dto.post.TodoRequest
import com.bucket.data.network.dto.post.TodoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class PostRemoteDataSource @Inject constructor(
    @param:DefaultNetwork
    private val client: HttpClient,
): PostDataSource {
    override suspend fun getPostDetail(postId: Long): BaseResponse<PostDetailResponse> =
        client.get("posts/$postId").body()

    override suspend fun toggleLike(postId: Long): BaseResponse<LikeResponse> =
        client.post("posts/$postId/likes").body()

    override suspend fun updatePost(
        postId: Long,
        request: PostUpdateRequest,
    ): BaseResponse<PostUpdateResponse> =
        client.patch("posts/$postId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun deletePost(postId: Long): BaseResponse<String?> =
        client.delete("posts/$postId").body()

    override suspend fun createSmallGoal(
        postId: Long,
        request: SmallGoalCreateRequest,
    ): BaseResponse<SmallGoalResponse> =
        client.post("posts/$postId/small-goals") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun updateSmallGoal(
        postId: Long,
        smallGoalId: Long,
        request: SmallGoalUpdateRequest,
    ): BaseResponse<SmallGoalResponse> =
        client.patch("posts/$postId/small-goals/$smallGoalId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun deleteSmallGoal(
        postId: Long,
        smallGoalId: Long,
    ): BaseResponse<String?> =
        client.delete("posts/$postId/small-goals/$smallGoalId").body()

    override suspend fun createTodo(
        postId: Long,
        smallGoalId: Long,
        request: TodoRequest,
    ): BaseResponse<TodoResponse> =
        client.post("posts/$postId/small-goals/$smallGoalId/todos") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun updateTodo(
        postId: Long,
        smallGoalId: Long,
        todoId: Long,
        request: TodoRequest,
    ): BaseResponse<TodoResponse> =
        client.patch("posts/$postId/small-goals/$smallGoalId/todos/$todoId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    override suspend fun deleteTodo(
        postId: Long,
        smallGoalId: Long,
        todoId: Long,
    ): BaseResponse<String?> =
        client.delete("posts/$postId/small-goals/$smallGoalId/todos/$todoId").body()
}
