package com.example.domain.usecase.post

import com.example.domain.repository.post.PostRepository
import javax.inject.Inject

/**
 * 만다라트 외곽 셀(SmallGoal/Plan) 추가.
 * POST /posts/{postId}/small-goals
 */
class CreateSmallGoalUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        postId: Long,
        content: String,
        color: String,
        isComplete: Boolean,
        sortOrder: Int,
    ) = postRepository.createSmallGoal(
        postId = postId,
        content = content,
        color = color,
        isComplete = isComplete,
        sortOrder = sortOrder,
    )
}
