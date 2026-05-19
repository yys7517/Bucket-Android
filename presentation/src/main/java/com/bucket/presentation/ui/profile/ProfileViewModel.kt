package com.bucket.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.home.PostCard
import com.example.domain.model.profile.ProfilePostStatus
import com.example.domain.model.profile.ProfilePostType
import com.example.domain.usecase.auth.GetSavedUserIdUseCase
import com.example.domain.usecase.profile.GetMyProfileUseCase
import com.example.domain.usecase.profile.GetProfilePostsUseCase
import com.example.domain.usecase.profile.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PostStatus(val label: String) {
    COMPLETED("완료"),
    IN_PROGRESS("진행 중"),
    DRAFT("대기")
}

enum class ProfileTab(val label: String) {
    POSTS("내 만다라트"),
    LIKED("좋아요"),
    BOOKMARKED("북마크")
}

data class ProfileUiState(
    val isProfileLoading: Boolean = false,
    val isPostsLoading: Boolean = false,
    val profileErrorMessage: String? = null,
    val postsErrorMessage: String? = null,
    val isMine: Boolean = true,
    val profileUserId: Long = 0L,
    val username: String = "",
    val userEmail: String = "",
    val introduction: String = "",
    val profileImgUrl: String = "",
    val bucketCount: Int = 0,
    val completedBucketCount: Int = 0,
    val totalLikeCount: Int = 0,
    val isFollowing: Boolean = false,
    val selectedTab: ProfileTab = ProfileTab.POSTS,
    val selectedStatus: ProfilePostStatus = ProfilePostStatus.ALL,
    val buckets: List<PostCard> = emptyList(),
) {
    val isLoading: Boolean
        get() = isProfileLoading || isPostsLoading

    val errorMessage: String?
        get() = profileErrorMessage ?: postsErrorMessage

    val displayedBuckets: List<PostCard>
        get() = buckets
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getSavedUserIdUseCase: GetSavedUserIdUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getProfilePostsUseCase: GetProfilePostsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile(userId: Long = 0L, isMine: Boolean = true) {
        _uiState.update {
            it.copy(
                isProfileLoading = true,
                isPostsLoading = false,
                profileErrorMessage = null,
                postsErrorMessage = null,
                isMine = isMine,
                profileUserId = userId,
                selectedTab = ProfileTab.POSTS,
                selectedStatus = ProfilePostStatus.ALL,
                buckets = emptyList(),
            )
        }

        viewModelScope.launch {
            val savedUserId = if (isMine) getSavedUserIdUseCase() else null
            val requestedUserId = savedUserId ?: userId

            _uiState.update {
                it.copy(profileUserId = requestedUserId)
            }

            val result = if (isMine) {
                getMyProfileUseCase()
            } else {
                getProfileUseCase(userId)
            }

            result
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            isProfileLoading = false,
                            profileErrorMessage = null,
                            profileUserId = profile.id,
                            username = profile.username,
                            userEmail = profile.email,
                            introduction = profile.introduction,
                            profileImgUrl = profile.profileImgUrl,
                            bucketCount = profile.postCount,
                            completedBucketCount = profile.completedPostCount,
                            totalLikeCount = profile.receivedLikeCount,
                        )
                    }
                    loadProfilePosts(
                        userId = requestedUserId.takeIf { it > 0L } ?: profile.id,
                        tab = ProfileTab.POSTS,
                        status = ProfilePostStatus.ALL,
                    )
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isProfileLoading = false,
                            isPostsLoading = false,
                            profileErrorMessage = throwable.message ?: "프로필 정보를 불러오지 못했습니다.",
                            buckets = emptyList(),
                        )
                    }
                }
        }
    }

    fun selectTab(tab: ProfileTab) {
        val userId = _uiState.value.profileUserId
        val status = _uiState.value.selectedStatus
        _uiState.update {
            it.copy(
                selectedTab = tab,
                postsErrorMessage = null,
                buckets = emptyList(),
            )
        }
        loadProfilePosts(userId = userId, tab = tab, status = status)
    }

    fun selectStatus(status: ProfilePostStatus) {
        val userId = _uiState.value.profileUserId
        val tab = _uiState.value.selectedTab
        _uiState.update {
            it.copy(
                selectedStatus = status,
                postsErrorMessage = null,
                buckets = emptyList(),
            )
        }
        loadProfilePosts(userId = userId, tab = tab, status = status)
    }

    fun toggleFollow() {
        _uiState.update { it.copy(isFollowing = !it.isFollowing) }
    }

    private fun loadProfilePosts(
        userId: Long,
        tab: ProfileTab,
        status: ProfilePostStatus,
    ) {
        if (userId <= 0L) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isPostsLoading = true,
                    postsErrorMessage = null,
                )
            }

            getProfilePostsUseCase(
                userId = userId,
                type = tab.toPostType(),
                status = status,
            ).onSuccess { buckets ->
                _uiState.update {
                    it.copy(
                        isPostsLoading = false,
                        postsErrorMessage = null,
                        buckets = buckets,
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isPostsLoading = false,
                        postsErrorMessage = throwable.message ?: "게시글을 불러오지 못했습니다.",
                        buckets = emptyList(),
                    )
                }
            }
        }
    }
}

private fun ProfileTab.toPostType(): ProfilePostType = when (this) {
    ProfileTab.POSTS -> ProfilePostType.MINE
    ProfileTab.LIKED -> ProfilePostType.LIKED
    ProfileTab.BOOKMARKED -> ProfilePostType.BOOKMARKED
}

fun PostCard.postStatus(): PostStatus {
    when (status.lowercase()) {
        ProfilePostStatus.COMPLETED.value -> return PostStatus.COMPLETED
        ProfilePostStatus.IN_PROGRESS.value -> return PostStatus.IN_PROGRESS
        ProfilePostStatus.DRAFT.value -> return PostStatus.DRAFT
    }

    if (totalCount > 0 && completedCount == totalCount) return PostStatus.COMPLETED

    return try {
        val start = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE)
        if (!start.isAfter(LocalDate.now())) PostStatus.IN_PROGRESS else PostStatus.DRAFT
    } catch (_: Exception) {
        PostStatus.DRAFT
    }
}
