package com.example.domain.usecase.post

import com.example.domain.repository.post.PostRepository
import javax.inject.Inject

class DeleteTodoUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: Long, smallGoalId: Long, todoId: Long) =
        postRepository.deleteTodo(postId, smallGoalId, todoId)
}
