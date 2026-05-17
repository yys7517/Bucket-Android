package com.bucket.data.repository

import com.bucket.data.datasource.post.PostDataSource
import com.bucket.data.mapper.asDomain
import com.bucket.data.mapper.asSmallGoal
import com.bucket.data.mapper.asTodo
import com.bucket.data.network.dto.post.PostUpdateRequest
import com.bucket.data.network.dto.post.SmallGoalCreateRequest
import com.bucket.data.network.dto.post.SmallGoalUpdateRequest
import com.bucket.data.network.dto.post.TodoRequest
import com.example.domain.model.post.LikeResult
import com.example.domain.model.post.PostDetail
import com.example.domain.model.post.PostUpdateResult
import com.example.domain.model.post.SmallGoal
import com.example.domain.model.post.Todo
import com.example.domain.model.user.Author
import com.example.domain.repository.post.PostRepository
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDataSource: PostDataSource
) : PostRepository {
    override suspend fun fetchPostDetail(postId: Long, author: Author): Result<PostDetail> = runCatching {
        postDataSource.getPostDetail(postId).data.asDomain(author)
    }

    override suspend fun toggleLike(postId: Long): Result<LikeResult> = runCatching {
        val resp = postDataSource.toggleLike(postId).data
        LikeResult(isLiked = resp.isLiked, likeCount = resp.likeCount)
    }

    override suspend fun updatePost(
        postId: Long,
        title: String,
        memo: String,
        startDate: String,
    ): Result<PostUpdateResult> = runCatching {
        postDataSource.updatePost(
            postId = postId,
            request = PostUpdateRequest(
                goal = title,
                memo = memo,
                startDate = startDate,
            )
        ).data.asDomain()
    }

    override suspend fun deletePost(postId: Long): Result<Unit> = runCatching {
        postDataSource.deletePost(postId)
    }

    override suspend fun createSmallGoal(
        postId: Long,
        content: String,
        color: String,
        isComplete: Boolean,
        sortOrder: Int,
    ): Result<SmallGoal> = runCatching {
        postDataSource.createSmallGoal(
            postId = postId,
            request = SmallGoalCreateRequest(
                content = content,
                color = color,
                isCompleted = isComplete,
                sortOrder = sortOrder,
            )
        ).data.asSmallGoal()
    }

    override suspend fun updateSmallGoal(
        postId: Long,
        smallGoalId: Long,
        content: String,
        color: String,
        isComplete: Boolean,
        sortOrder: Int,
    ): Result<SmallGoal> = runCatching {
        postDataSource.updateSmallGoal(
            postId = postId,
            smallGoalId = smallGoalId,
            request = SmallGoalUpdateRequest(
                content = content,
                color = color,
                isCompleted = isComplete,
                sortOrder = sortOrder,
            )
        ).data.asSmallGoal()
    }

    override suspend fun deleteSmallGoal(
        postId: Long,
        smallGoalId: Long,
    ): Result<Unit> = runCatching {
        postDataSource.deleteSmallGoal(postId, smallGoalId)
        Unit
    }

    override suspend fun createTodo(
        postId: Long,
        smallGoalId: Long,
        content: String,
        color: String,
        isComplete: Boolean,
        sortOrder: Int,
    ): Result<Todo> = runCatching {
        postDataSource.createTodo(
            postId = postId,
            smallGoalId = smallGoalId,
            request = TodoRequest(
                content = content,
                color = color,
                isCompleted = isComplete,
                sortOrder = sortOrder,
            )
        ).data.asTodo()
    }

    override suspend fun updateTodo(
        postId: Long,
        smallGoalId: Long,
        todoId: Long,
        content: String,
        color: String,
        isComplete: Boolean,
        sortOrder: Int,
    ): Result<Todo> = runCatching {
        postDataSource.updateTodo(
            postId = postId,
            smallGoalId = smallGoalId,
            todoId = todoId,
            request = TodoRequest(
                content = content,
                color = color,
                isCompleted = isComplete,
                sortOrder = sortOrder,
            )
        ).data.asTodo()
    }

    override suspend fun deleteTodo(
        postId: Long,
        smallGoalId: Long,
        todoId: Long,
    ): Result<Unit> = runCatching {
        postDataSource.deleteTodo(postId, smallGoalId, todoId)
        Unit
    }
}
