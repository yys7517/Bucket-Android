package com.bucket.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.home.PopularBucket
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
    val popularBuckets: List<PopularBucket> = emptyList(),
    val selectedCategory: String = "전체",
    val errorMessage: String? = null
) {
    val filteredBuckets: List<PopularBucket>
        get() = if (selectedCategory == "전체") popularBuckets
                else popularBuckets.filter { it.category == selectedCategory }
}

sealed interface HomeEvent {
    data class BookmarkUpdated(val isBookmarked: Boolean) : HomeEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
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
                    isLoading = state.popularBuckets.isEmpty(),
                    errorMessage = null
                )
            }

            getPopularBucketsUseCase()
                .onSuccess { popularBuckets ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            popularBuckets = state.popularBuckets.reuseUnchangedItems(popularBuckets)
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = if (state.popularBuckets.isEmpty()) {
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

    fun toggleBookmark(postId: Long) {
        val currentBuckets = _uiState.value.popularBuckets
        val target = currentBuckets.firstOrNull { it.id == postId } ?: return
        val optimisticBuckets = currentBuckets.updateBookmark(
            postId = postId,
            isBookmarked = !target.isBookmarked
        )

        _uiState.update { it.copy(popularBuckets = optimisticBuckets) }

        viewModelScope.launch {
            toggleBookmarkUseCase(postId)
                .onSuccess { result ->
                    _uiState.update { state ->
                        state.copy(
                            popularBuckets = state.popularBuckets.updateBookmark(
                                postId = postId,
                                isBookmarked = result.isBookmarked
                            )
                        )
                    }
                    _events.trySend(HomeEvent.BookmarkUpdated(result.isBookmarked))
                }
                .onFailure {
                    _uiState.update { it.copy(popularBuckets = currentBuckets) }
                }
        }
    }

    private fun List<PopularBucket>.reuseUnchangedItems(next: List<PopularBucket>): List<PopularBucket> {
        val previousById = associateBy { it.id }
        return next.map { bucket ->
            previousById[bucket.id]?.takeIf { it == bucket } ?: bucket
        }
    }

    private fun List<PopularBucket>.updateBookmark(
        postId: Long,
        isBookmarked: Boolean,
    ): List<PopularBucket> = map { bucket ->
        if (bucket.id == postId) bucket.copy(isBookmarked = isBookmarked) else bucket
    }
}
