package com.bucket.data.network.dto.post

import com.bucket.data.network.dto.user.UserInfoResponse
import kotlinx.serialization.Serializable


@Serializable
data class PostDetailResponse(
    val id: Long,
    val category: String,
    val categoryColor: String,
    val title: String,
    val memo: String,
    val likeCount: Int,
    val startDate: String,
    val userInfo: UserInfoResponse,
    val plans: List<PostPlanDetailResponse>
    // TODO. val isMine: Boolean
)

/*
        "id": 1,
        "category": "여행",
        "categoryColor": "#3B82F6",
        "title": "한라산 백록담 등반하기",
        "memo": "올해 안에 날씨 좋은 날을 골라 한라산 정상까지 올라가기",
        "likeCount": 1,
        "startDate": "2026-05-22",
        "userInfo": {
            "id": 1,
            "email": "test@example.com",
            "username": "test-user",
            "profileImgUrl": "https://example.com/test-user.png"
        },
        "plans": [
            {
                "id": 1,
                "sortOrder": 1,
                "content": "항공권 예약하기",
                "isComplete": true
            },
            {
                "id": 2,
                "sortOrder": 2,
                "content": "등산화 점검하기",
                "isComplete": true
            },
            {
                "id": 3,
                "sortOrder": 3,
                "content": "성판악 코스 예약하기",
                "isComplete": false
            }
        ]
 */

