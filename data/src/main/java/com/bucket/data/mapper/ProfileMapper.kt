package com.bucket.data.mapper

import com.bucket.data.network.dto.profile.ProfileResponse
import com.example.domain.model.profile.Profile

fun ProfileResponse.asDomain(): Profile = Profile(
    id = id,
    email = email,
    username = username,
    introduction = introduction.ifBlank { bio },
    profileImgUrl = profileImgUrl,
    postCount = postCount,
    completedPostCount = completedPostCount,
    receivedLikeCount = receivedLikeCount,
)
