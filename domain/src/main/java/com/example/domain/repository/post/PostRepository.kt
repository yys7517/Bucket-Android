package com.example.domain.repository.post

import com.example.domain.model.post.BucketPostDetail

interface PostRepository {
    suspend fun fetchPostDetail(postId: Long): Result<BucketPostDetail>
}