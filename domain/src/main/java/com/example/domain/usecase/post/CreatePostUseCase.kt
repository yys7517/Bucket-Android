package com.example.domain.usecase.post

import com.example.domain.model.user.Author
import com.example.domain.repository.post.PostRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        goal: String,
        categoryId: Long,
        startDate: String?,
        memo: String,
        author: Author,
    ) = postRepository.createPost(
        goal = goal,
        categoryId = categoryId,
        startDate = startDate,
        memo = memo,
        author = author,
    )
}
