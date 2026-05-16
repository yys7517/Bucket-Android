package com.example.domain.usecase.post

import com.example.domain.repository.post.PostRepository
import javax.inject.Inject

class ToggleLikeUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: Long) = postRepository.toggleLike(postId)
}
