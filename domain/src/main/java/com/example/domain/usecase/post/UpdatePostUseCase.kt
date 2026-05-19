package com.example.domain.usecase.post

import com.example.domain.repository.post.PostRepository
import javax.inject.Inject

/** 게시글(태스크) 본문 수정 — 목표 이름 / 시작일 / 메모 */
class UpdatePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        postId: Long,
        title: String,
        memo: String,
        startDate: String?,
    ) = postRepository.updatePost(
        postId = postId,
        title = title,
        memo = memo,
        startDate = startDate,
    )
}
