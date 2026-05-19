package com.bucket.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.home.PostCard
import com.example.domain.usecase.auth.GetSavedUserIdUseCase
import com.example.domain.usecase.home.GetPopularBucketsUseCase
import com.example.domain.usecase.post.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class HomeUiState(
    val isLoading: Boolean = false,
    val postCards: List<PostCard> = emptyList(),
    val selectedCategory: String = "전체",
    val errorMessage: String? = null
) {
    val filteredBuckets: List<PostCard>
        get() = if (selectedCategory == "전체") postCards
                else postCards.filter { it.category == selectedCategory }
}

sealed interface HomeEvent {
    data class BookmarkUpdated(val isBookmarked: Boolean) : HomeEvent
    data class ProfileRequested(val userId: Long, val isMine: Boolean) : HomeEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSavedUserIdUseCase: GetSavedUserIdUseCase,
    private val getPopularBucketsUseCase: GetPopularBucketsUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    private var loadHomeJob: Job? = null

    init {
        loadHome()
    }

    fun loadHome() {
        if (loadHomeJob?.isActive == true) return

        loadHomeJob = viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = state.postCards.isEmpty(),
                    errorMessage = null
                )
            }

            getPopularBucketsUseCase()
                .onSuccess { popularBuckets ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            postCards = state.postCards.reuseUnchangedItems(popularBuckets)
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = if (state.postCards.isEmpty()) {
                                throwable.message ?: "홈 정보를 불러오지 못했습니다."
                            } else {
                                null
                            }
                        )
                    }
                }
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun openAuthorProfile(userId: Long) {
        if (userId <= 0L) return

        viewModelScope.launch {
            val savedUserId = getSavedUserIdUseCase()
            _events.trySend(
                HomeEvent.ProfileRequested(
                    userId = userId,
                    isMine = savedUserId == userId
                )
            )
        }
    }

    fun toggleBookmark(postId: Long) {
        val currentBuckets = _uiState.value.postCards
        val target = currentBuckets.firstOrNull { it.id == postId } ?: return
        val optimisticBuckets = currentBuckets.updateBookmark(
            postId = postId,
            isBookmarked = !target.isBookmarked
        )

        _uiState.update { it.copy(postCards = optimisticBuckets) }

        viewModelScope.launch {
            toggleBookmarkUseCase(postId)
                .onSuccess { result ->
                    _uiState.update { state ->
                        state.copy(
                            postCards = state.postCards.updateBookmark(
                                postId = postId,
                                isBookmarked = result.isBookmarked
                            )
                        )
                    }
                    _events.trySend(HomeEvent.BookmarkUpdated(result.isBookmarked))
                }
                .onFailure {
                    _uiState.update { it.copy(postCards = currentBuckets) }
                }
        }
    }

    private fun List<PostCard>.reuseUnchangedItems(next: List<PostCard>): List<PostCard> {
        val previousById = associateBy { it.id }
        return next.map { bucket ->
            previousById[bucket.id]?.takeIf { it == bucket } ?: bucket
        }
    }

    private fun List<PostCard>.updateBookmark(
        postId: Long,
        isBookmarked: Boolean,
    ): List<PostCard> = map { bucket ->
        if (bucket.id == postId) bucket.copy(isBookmarked = isBookmarked) else bucket
    }
}
