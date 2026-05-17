package com.example.domain.usecase.post

import com.example.domain.repository.post.PostRepository
import javax.inject.Inject

/**
 * 만다라트 외곽 셀(SmallGoal/Plan) 삭제.
 * DELETE /posts/{postId}/small-goals/{smallGoalId}
 */
class DeleteSmallGoalUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: Long, smallGoalId: Long) =
        postRepository.deleteSmallGoal(postId, smallGoalId)
}
