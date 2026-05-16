package com.example.domain.repository.post

import com.example.domain.model.post.BucketPostDetail
import com.example.domain.model.post.LikeResult
import com.example.domain.model.user.Author

interface PostRepository {
    suspend fun fetchPostDetail(postId: Long, author: Author): Result<BucketPostDetail>
    suspend fun toggleLike(postId: Long): Result<LikeResult>
}