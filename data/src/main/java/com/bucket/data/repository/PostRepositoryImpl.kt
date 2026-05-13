package com.bucket.data.repository

import com.bucket.data.datasource.post.PostDataSource
import com.bucket.data.mapper.asDomain
import com.example.domain.model.post.BucketPostDetail
import com.example.domain.repository.post.PostRepository
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDataSource: PostDataSource
): PostRepository {
    override suspend fun fetchPostDetail(postId: Long): Result<BucketPostDetail> = runCatching {
        postDataSource.getPostDetail(postId).data.asDomain()
    }

}
