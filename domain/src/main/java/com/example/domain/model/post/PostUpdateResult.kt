package com.example.domain.model.post

/**
 * 게시글(태스크) 본문 수정 결과.
 * PATCH /posts/{postId} 응답의 data 필드를 도메인 모델로 표현한다.
 */
data class PostUpdateResult(
    val id: Long,
    val title: String,
    val memo: String,
    val startDate: String,
)
