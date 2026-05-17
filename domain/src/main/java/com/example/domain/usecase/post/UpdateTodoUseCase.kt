package com.example.domain.usecase.post

import com.example.domain.repository.post.PostRepository
import javax.inject.Inject

class UpdateTodoUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        postId: Long,
        smallGoalId: Long,
        todoId: Long,
        content: String,
        color: String,
        isComplete: Boolean,
        sortOrder: Int,
    ) = postRepository.updateTodo(
        postId = postId,
        smallGoalId = smallGoalId,
        todoId = todoId,
        content = content,
        color = color,
        isComplete = isComplete,
        sortOrder = sortOrder,
    )
}
