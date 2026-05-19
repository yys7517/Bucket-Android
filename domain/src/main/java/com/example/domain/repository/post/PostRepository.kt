package com.example.domain.repository.post

import com.example.domain.model.post.PostDetail
import com.example.domain.model.post.PostUpdateResult
import com.example.domain.model.post.BookmarkResult
import com.example.domain.model.post.LikeResult
import com.example.domain.model.post.Todo
import com.example.domain.model.post.SmallGoal
import com.example.domain.model.user.Author

interface PostRepository {
    suspend fun createPost(
        goal: String,
        categoryId: Long,
        startDate: String?,
        memo: String,
        author: Author,
    ): Result<PostDetail>
    suspend fun fetchPostDetail(postId: Long, author: Author): Result<PostDetail>
    suspend fun toggleLike(postId: Long): Result<LikeResult>
    suspend fun toggleBookmark(postId: Long): Result<BookmarkResult>

    /** PATCH /posts/{postId} — 목표 이름/시작일/메모 수정 */
    suspend fun updatePost(
        postId: Long,
        title: String,
        memo: String,
        startDate: String?,
    ): Result<PostUpdateResult>

    /** DELETE /posts/{postId} — 게시글 삭제 */
    suspend fun deletePost(postId: Long): Result<Unit>

    /** POST /posts/{postId}/small-goals — 만다라트 외곽 셀 추가 */
    suspend fun createSmallGoal(
        postId: Long,
        content: String,
        color: String,
        isComplete: Boolean,
        sortOrder: Int,
    ): Result<SmallGoal>

    /** PATCH /posts/{postId}/small-goals/{smallGoalId} — 만다라트 외곽 셀 수정 */
    suspend fun updateSmallGoal(
        postId: Long,
        smallGoalId: Long,
        content: String,
        color: String,
        isComplete: Boolean,
        sortOrder: Int,
    ): Result<SmallGoal>

    /** DELETE /posts/{postId}/small-goals/{smallGoalId} — 만다라트 외곽 셀 삭제 */
    suspend fun deleteSmallGoal(postId: Long, smallGoalId: Long): Result<Unit>

    /** POST /posts/{postId}/small-goals/{smallGoalId}/todos — 만다라트 내부 Todo 추가 */
    suspend fun createTodo(
        postId: Long,
        smallGoalId: Long,
        content: String,
        color: String,
        isComplete: Boolean,
        sortOrder: Int,
    ): Result<Todo>

    /** PATCH /posts/{postId}/small-goals/{smallGoalId}/todos/{todoId} — 만다라트 내부 Todo 수정 */
    suspend fun updateTodo(
        postId: Long,
        smallGoalId: Long,
        todoId: Long,
        content: String,
        color: String,
        isComplete: Boolean,
        sortOrder: Int,
    ): Result<Todo>

    /** DELETE /posts/{postId}/small-goals/{smallGoalId}/todos/{todoId} — 만다라트 내부 Todo 삭제 */
    suspend fun deleteTodo(postId: Long, smallGoalId: Long, todoId: Long): Result<Unit>
}
