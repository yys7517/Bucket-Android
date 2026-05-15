package com.bucket.data.mapper

import com.bucket.data.network.dto.post.PostDetailResponse
import com.bucket.data.network.dto.post.PostPlanDetailResponse
import com.example.domain.model.post.BucketPostDetail
import com.example.domain.model.post.PostPlan

fun PostDetailResponse.asDomain(): BucketPostDetail = BucketPostDetail(
    id = this.id,
    title = this.title,
    memo = this.memo,
    category = this.category,
    categoryColor = this.categoryColor,
    likeCount = this.likeCount,
    startDate = this.startDate,
    userId = this.userInfo.id,
    username = this.userInfo.username,
    profileImage = this.userInfo.profileImgUrl,
    plans = this.plans.map { it.asPostPlan() },
    isLiked = isLiked,
    isMine = isMine
)

fun PostPlanDetailResponse.asPostPlan(): PostPlan = PostPlan(
    id = this.id,
    sortOrder = this.sortOrder,
    content = this.content,
    isComplete = this.isComplete,
)
