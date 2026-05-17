package com.bucket.presentation.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bucket.presentation.theme.BucketappTheme
import com.example.domain.model.post.PostDetail
import com.example.domain.model.post.SmallGoal
import com.example.domain.model.post.Todo
import com.example.domain.model.user.Author

@Preview(showBackground = true, widthDp = 430, heightDp = 932)
@Composable
private fun MandalaGridPreview() {
    val plans = listOf(
        SmallGoal(
            id = 1,
            sortOrder = 1,
            content = "면접 준비",
            isComplete = true,
            color = "#8D6BE8",
            todos = listOf(Todo(11, "모의면접", "#8D6BE8", true))
        ),
        SmallGoal(id = 2, sortOrder = 2, content = "자기소개서", isComplete = false, color = "#E8736B"),
        SmallGoal(id = 3, sortOrder = 3, content = "포트폴리오", isComplete = false, color = "#E8A06B"),
        SmallGoal(id = 4, sortOrder = 4, content = "인적성", isComplete = false, color = "#6BE88D"),
        SmallGoal(id = 5, sortOrder = 5, content = "CS스터디", isComplete = true, color = "#E8C96B"),
        SmallGoal(id = 6, sortOrder = 6, content = "체력관리", isComplete = false, color = "#6BB8E8"),
        SmallGoal(id = 7, sortOrder = 7, content = "어학성적", isComplete = false, color = "#6BE8D4"),
        SmallGoal(id = 8, sortOrder = 8, content = "코딩테스트", isComplete = false, color = "#E86BB8"),
    )
    BucketappTheme(dynamicColor = false) {
        PostDetailScreen(
            uiState = PostDetailUiState(
                postDetail = PostDetail(
                    id = 13,
                    title = "대기업 입사",
                    memo = "내년 상반기 공채 합격이 목표.",
                    category = "학습",
                    categoryColor = "#8D6BE8",
                    likeCount = 243,
                    startDate = "2026-05-13",
                    author = Author(userId = 2, username = "jiwon", profileImgUrl = ""),
                    smallGoals = plans,
                    isLiked = true,
                    isMine = true
                )
            ),
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 430, heightDp = 932)
@Composable
private fun MandalaModalPreview() {
    val plan = SmallGoal(
        id = 1,
        sortOrder = 1,
        content = "면접 준비",
        isComplete = true,
        color = "#8D6BE8",
        todos = listOf(
            Todo(11, "모의면접", "#E8736B", true),
            Todo(12, "자기 PR 정리", "#8D6BE8", true),
            Todo(13, "꼬리질문 대비", "#6BE88D", false),
            Todo(14, "AI 면접", "#6BB8E8", false),
            Todo(15, "면접 복장", "#E8A06B", false),
        )
    )
    BucketappTheme(dynamicColor = false) {
        PostDetailScreen(
            uiState = PostDetailUiState(
                selectedMandalaCell = plan,
                postDetail = PostDetail(
                    id = 13,
                    title = "대기업 입사",
                    memo = "",
                    category = "학습",
                    categoryColor = "#8D6BE8",
                    likeCount = 243,
                    startDate = "2026-05-13",
                    author = Author(userId = 2, username = "jiwon", profileImgUrl = ""),
                    smallGoals = listOf(plan),
                    isLiked = true,
                    isMine = true
                )
            ),
            onBackClick = {}
        )
    }
}
