package com.bucket.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.home.PopularBucket
import com.example.domain.model.home.RecentBucket
import com.example.domain.usecase.home.GetPopularBucketsUseCase
import com.example.domain.usecase.home.GetRecentBucketsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val popularBuckets: List<PopularBucket> = emptyList(),
    val recentBuckets: List<RecentBucket> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPopularBucketsUseCase: GetPopularBucketsUseCase,
    private val getRecentBucketsUseCase: GetRecentBucketsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching {
                coroutineScope {
                    val popular = async { getPopularBucketsUseCase() }
                    val recent = async { getRecentBucketsUseCase() }
                    popular.await().getOrThrow() to recent.await().getOrThrow()
                }
            }.onSuccess { (popularBuckets, recentBuckets) ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        popularBuckets = popularBuckets,
                        recentBuckets = recentBuckets
                    )
                }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "홈 정보를 불러오지 못했습니다."
                    )
                }
            }
        }
    }
}
