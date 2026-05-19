package com.bucket.data.mapper

import com.bucket.data.network.dto.profile.ProfileResponse
import com.bucket.data.network.dto.profile.ProfileUpdateResponse
import com.example.domain.model.profile.Profile
import com.example.domain.model.profile.ProfileUpdateResult

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

fun ProfileUpdateResponse.asDomain(): ProfileUpdateResult = ProfileUpdateResult(
    username = username,
    email = email,
    introduction = introduction,
)
