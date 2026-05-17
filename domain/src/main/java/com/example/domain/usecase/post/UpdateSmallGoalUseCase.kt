package com.example.domain.usecase.post

import com.example.domain.repository.post.PostRepository
import javax.inject.Inject

/**
 * 만다라트 외곽 셀(SmallGoal/Plan) 수정.
 * PATCH /posts/{postId}/small-goals/{smallGoalId}
 */
class UpdateSmallGoalUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        postId: Long,
        smallGoalId: Long,
        content: String,
        color: String,
        isComplete: Boolean,
        sortOrder: Int,
    ) = postRepository.updateSmallGoal(
        postId = postId,
        smallGoalId = smallGoalId,
        content = content,
        color = color,
        isComplete = isComplete,
        sortOrder = sortOrder,
    )
}
