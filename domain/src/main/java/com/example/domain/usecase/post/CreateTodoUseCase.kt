package com.example.domain.usecase.post

import com.example.domain.repository.post.PostRepository
import javax.inject.Inject

class CreateTodoUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        postId: Long,
        smallGoalId: Long,
        content: String,
        color: String,
        isComplete: Boolean,
        sortOrder: Int,
    ) = postRepository.createTodo(
        postId = postId,
        smallGoalId = smallGoalId,
        content = content,
        color = color,
        isComplete = isComplete,
        sortOrder = sortOrder,
    )
}
