package com.bucket.data.mapper

import com.bucket.data.network.dto.post.PostDetailResponse
import com.bucket.data.network.dto.post.PostUpdateResponse
import com.bucket.data.network.dto.post.SmallGoalResponse
import com.bucket.data.network.dto.post.TodoResponse
import com.example.domain.model.post.PostDetail
import com.example.domain.model.post.PostUpdateResult
import com.example.domain.model.post.SmallGoal
import com.example.domain.model.post.Todo
import com.example.domain.model.user.Author

fun PostDetailResponse.asDomain(author: Author): PostDetail = PostDetail(
    id = this.id,
    title = this.goal,
    memo = this.memo,
    category = this.category,
    categoryColor = this.categoryColor,
    likeCount = this.likeCount,
    startDate = this.startDate.orEmpty(),
    author = author,
    smallGoals = this.smallGoals.map { it.asSmallGoal() },
    isLiked = isLiked,
    isMine = isMine,
    isBookmarked = isBookmarked,
)

fun SmallGoalResponse.asSmallGoal(): SmallGoal = SmallGoal(
    id = this.id ?: 0L,
    sortOrder = this.sortOrder,
    content = this.content,
    isComplete = this.isCompleted ?: this.isComplete ?: false,
    color = this.color,
    todos = this.todos.map { it.asTodo() },
)

fun TodoResponse.asTodo(): Todo = Todo(
    id = this.id,
    content = this.content,
    color = this.color,
    isComplete = this.isCompleted ?: this.isComplete ?: false,
    position = this.sortOrder,
)

fun PostUpdateResponse.asDomain(): PostUpdateResult = PostUpdateResult(
    id = this.id,
    title = this.goal,
    memo = this.memo,
    startDate = this.startDate.orEmpty(),
)
