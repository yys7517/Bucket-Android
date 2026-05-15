package com.example.domain.usecase.post

import com.example.domain.model.user.Author
import com.example.domain.repository.post.PostRepository
import javax.inject.Inject

class GetPostDetailUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: Long, author: Author) =
        postRepository.fetchPostDetail(postId, author)
}
