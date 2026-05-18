package com.bucket.presentation.ui.profile

import androidx.lifecycle.ViewModel
import com.example.domain.model.home.PopularBucket
import com.example.domain.model.home.SmallGoalSummary
import com.example.domain.model.user.Author
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// ─── 상태 ─────────────────────────────────────────────────────────────────────

enum class BucketStatus(val label: String) {
    DONE("완료"),
    IN_PROGRESS("진행 중"),
    PENDING("대기")
}

enum class ProfileTab(val label: String) {
    MY_BUCKETS("내 만다라트"),
    LIKED("좋아요"),
    SAVED("저장")
}

data class ProfileUiState(
    val isMine: Boolean = true,
    val profileUserId: Long = 0L,
    val username: String = "",
    val userId: String = "",          // @아이디
    val bio: String = "",             // 자기소개
    val profileImgUrl: String = "",
    val bucketCount: Int = 0,         // 만다라트 총 수
    val completedBucketCount: Int = 0, // 완료한 만다라트 수
    val totalLikeCount: Int = 0,      // 받은 좋아요 합산
    val isFollowing: Boolean = false,
    val selectedTab: ProfileTab = ProfileTab.MY_BUCKETS,
    val myBuckets: List<PopularBucket> = emptyList(),
    val likedBuckets: List<PopularBucket> = emptyList(),
    val savedBuckets: List<PopularBucket> = emptyList(),
) {
    /** 현재 탭에 따라 표시할 버킷 목록 */
    val displayedBuckets: List<PopularBucket>
        get() = when (selectedTab) {
            ProfileTab.MY_BUCKETS -> myBuckets
            ProfileTab.LIKED     -> likedBuckets
            ProfileTab.SAVED     -> savedBuckets
        }
}

// ─── ViewModel ───────────────────────────────────────────────────────────────

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    /** 프로필 로드 (isMine=true 이면 내 프로필, false 이면 상대방 프로필) */
    fun loadProfile(userId: Long = 0L, isMine: Boolean = true) {
        val (myBuckets, likedBuckets, savedBuckets) = if (isMine) {
            Triple(MOCK_MY_BUCKETS, MOCK_LIKED_BUCKETS, MOCK_SAVED_BUCKETS)
        } else {
            Triple(MOCK_OTHER_BUCKETS, emptyList(), emptyList())
        }
        val completedCount = myBuckets.count { it.bucketStatus() == BucketStatus.DONE }
        val likeSum = myBuckets.sumOf { it.likeCount }

        _uiState.update {
            it.copy(
                isMine = isMine,
                profileUserId = userId,
                username = if (isMine) "윤영선" else "traveler_jo",
                userId = if (isMine) "youngseon" else "traveler_jo",
                bio = if (isMine) "한 칸씩 채워가는 중. 백엔드 개발자." else "여행이 삶이다. 동남아 전문 탐험가.",
                profileImgUrl = "",
                bucketCount = myBuckets.size,
                completedBucketCount = completedCount,
                totalLikeCount = likeSum,
                myBuckets = myBuckets,
                likedBuckets = likedBuckets,
                savedBuckets = savedBuckets,
                selectedTab = ProfileTab.MY_BUCKETS,
            )
        }
    }

    fun selectTab(tab: ProfileTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleFollow() {
        _uiState.update { it.copy(isFollowing = !it.isFollowing) }
    }
}

// ─── 버킷 상태 결정 헬퍼 ─────────────────────────────────────────────────────

fun PopularBucket.bucketStatus(): BucketStatus {
    // 전체 소목표가 완료 → 완료
    if (totalCount > 0 && completedCount == totalCount) return BucketStatus.DONE
    // startDate 가 오늘 이전 → 진행 중
    return try {
        val start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE)
        if (!start.isAfter(LocalDate.now())) BucketStatus.IN_PROGRESS else BucketStatus.PENDING
    } catch (_: Exception) {
        BucketStatus.PENDING
    }
}

// ─── 목 데이터 ────────────────────────────────────────────────────────────────

private val MOCK_AUTHOR_ME = Author(userId = 1L, username = "youngsun", profileImgUrl = "")

private val MOCK_MY_BUCKETS = listOf(
    PopularBucket(
        id = 101L,
        category = "학습",
        categoryColor = "#7B5DD6",
        title = "Spring 백엔드 완성",
        author = MOCK_AUTHOR_ME,
        likeCount = 18,
        isLiked = false,
        startDate = "2026-04-10",
        completedCount = 8,
        totalCount = 8,
        progressRate = 100,
        smallGoals = mapOf(
            1 to SmallGoalSummary("Spring Boot", "#7B5DD6", true),
            2 to SmallGoalSummary("JPA", "#7B5DD6", true),
            3 to SmallGoalSummary("Security", "#7B5DD6", true),
            4 to SmallGoalSummary("JWT", "#7B5DD6", true),
            6 to SmallGoalSummary("Redis", "#7B5DD6", true),
            7 to SmallGoalSummary("Docker", "#7B5DD6", true),
            8 to SmallGoalSummary("CI/CD", "#7B5DD6", true),
            9 to SmallGoalSummary("테스트", "#7B5DD6", true),
        )
    ),
    PopularBucket(
        id = 102L,
        category = "여행",
        categoryColor = "#2C8BAA",
        title = "유럽 배낭여행 30일",
        author = MOCK_AUTHOR_ME,
        likeCount = 42,
        isLiked = true,
        startDate = "2026-05-01",
        completedCount = 3,
        totalCount = 8,
        progressRate = 37,
        smallGoals = mapOf(
            1 to SmallGoalSummary("항공권 예매", "#2C8BAA", true),
            2 to SmallGoalSummary("숙소 예약", "#2C8BAA", true),
            3 to SmallGoalSummary("유레일패스", "#2C8BAA", true),
            4 to SmallGoalSummary("파리 일정", "#2C8BAA", false),
            6 to SmallGoalSummary("로마 일정", "#2C8BAA", false),
            7 to SmallGoalSummary("바르셀로나", "#2C8BAA", false),
            8 to SmallGoalSummary("여행보험", "#2C8BAA", false),
            9 to SmallGoalSummary("환전", "#2C8BAA", false),
        )
    ),
    PopularBucket(
        id = 103L,
        category = "운동",
        categoryColor = "#2F9B68",
        title = "마라톤 풀코스 완주",
        author = MOCK_AUTHOR_ME,
        likeCount = 9,
        isLiked = false,
        startDate = "2026-06-01",
        completedCount = 0,
        totalCount = 8,
        progressRate = 0,
        smallGoals = mapOf(
            1 to SmallGoalSummary("5km 달리기", "#2F9B68", false),
            2 to SmallGoalSummary("10km 달리기", "#2F9B68", false),
            3 to SmallGoalSummary("하프 마라톤", "#2F9B68", false),
            4 to SmallGoalSummary("식단 관리", "#2F9B68", false),
            6 to SmallGoalSummary("스트레칭", "#2F9B68", false),
            7 to SmallGoalSummary("대회 등록", "#2F9B68", false),
            8 to SmallGoalSummary("페이스 훈련", "#2F9B68", false),
            9 to SmallGoalSummary("완주 목표", "#2F9B68", false),
        )
    ),
    PopularBucket(
        id = 104L,
        category = "취미",
        categoryColor = "#D57931",
        title = "사진 작가 되기",
        author = MOCK_AUTHOR_ME,
        likeCount = 31,
        isLiked = false,
        startDate = "2026-03-15",
        completedCount = 2,
        totalCount = 8,
        progressRate = 25,
        smallGoals = mapOf(
            1 to SmallGoalSummary("카메라 구매", "#D57931", true),
            2 to SmallGoalSummary("구도 공부", "#D57931", true),
            3 to SmallGoalSummary("조명 연구", "#D57931", false),
            4 to SmallGoalSummary("라이트룸", "#D57931", false),
            6 to SmallGoalSummary("포트폴리오", "#D57931", false),
            7 to SmallGoalSummary("전시 참가", "#D57931", false),
            8 to SmallGoalSummary("SNS 운영", "#D57931", false),
            9 to SmallGoalSummary("출판 도전", "#D57931", false),
        )
    ),
)

private val MOCK_LIKED_BUCKETS = listOf(
    PopularBucket(
        id = 201L,
        category = "독서",
        categoryColor = "#8D6BE8",
        title = "1년 100권 독서",
        author = Author(userId = 2L, username = "book_lover", profileImgUrl = ""),
        likeCount = 55,
        isLiked = true,
        startDate = "2026-01-01",
        completedCount = 5,
        totalCount = 8,
        progressRate = 62,
        smallGoals = mapOf(
            1 to SmallGoalSummary("소설 30권", "#8D6BE8", true),
            2 to SmallGoalSummary("자기계발 20권", "#8D6BE8", true),
            3 to SmallGoalSummary("인문학 15권", "#8D6BE8", true),
            4 to SmallGoalSummary("기술서 15권", "#8D6BE8", true),
            6 to SmallGoalSummary("독서 노트", "#8D6BE8", true),
            7 to SmallGoalSummary("독서 모임", "#8D6BE8", false),
            8 to SmallGoalSummary("블로그 리뷰", "#8D6BE8", false),
            9 to SmallGoalSummary("북클럽 운영", "#8D6BE8", false),
        )
    ),
)

private val MOCK_SAVED_BUCKETS = listOf(
    PopularBucket(
        id = 301L,
        category = "재테크",
        categoryColor = "#E8A020",
        title = "1억 자산 만들기",
        author = Author(userId = 3L, username = "invest_master", profileImgUrl = ""),
        likeCount = 78,
        isLiked = false,
        startDate = "2026-01-01",
        completedCount = 2,
        totalCount = 8,
        progressRate = 25,
        smallGoals = mapOf(
            1 to SmallGoalSummary("비상금 500만", "#E8A020", true),
            2 to SmallGoalSummary("주식 공부", "#E8A020", true),
            3 to SmallGoalSummary("ETF 투자", "#E8A020", false),
            4 to SmallGoalSummary("부동산 공부", "#E8A020", false),
            6 to SmallGoalSummary("절약 습관", "#E8A020", false),
            7 to SmallGoalSummary("부업 시작", "#E8A020", false),
            8 to SmallGoalSummary("연금 설정", "#E8A020", false),
            9 to SmallGoalSummary("목표 달성", "#E8A020", false),
        )
    ),
)

private val MOCK_OTHER_BUCKETS = listOf(
    PopularBucket(
        id = 401L,
        category = "여행",
        categoryColor = "#2C8BAA",
        title = "동남아 배낭여행",
        author = Author(userId = 5L, username = "traveler_jo", profileImgUrl = ""),
        likeCount = 34,
        isLiked = false,
        startDate = "2026-04-01",
        completedCount = 4,
        totalCount = 8,
        progressRate = 50,
        smallGoals = mapOf(
            1 to SmallGoalSummary("태국", "#2C8BAA", true),
            2 to SmallGoalSummary("베트남", "#2C8BAA", true),
            3 to SmallGoalSummary("캄보디아", "#2C8BAA", true),
            4 to SmallGoalSummary("인도네시아", "#2C8BAA", true),
            6 to SmallGoalSummary("필리핀", "#2C8BAA", false),
            7 to SmallGoalSummary("말레이시아", "#2C8BAA", false),
            8 to SmallGoalSummary("싱가포르", "#2C8BAA", false),
            9 to SmallGoalSummary("귀국", "#2C8BAA", false),
        )
    ),
)
