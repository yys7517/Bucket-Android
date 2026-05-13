package com.bucket.presentation.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.post.BucketPostDetail
import com.example.domain.usecase.post.GetPostDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BucketDetailUiState(
    val isLoading: Boolean = false,
    val bucketDetail: BucketPostDetail? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class BucketDetailViewModel @Inject constructor(
    private val getPostDetailUseCase: GetPostDetailUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(BucketDetailUiState())
    val uiState: StateFlow<BucketDetailUiState> = _uiState.asStateFlow()

    fun loadBucketDetail(bucketId: Long) {
        if (bucketId <= 0L) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "잘못된 게시글입니다."
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getPostDetailUseCase(bucketId)
                .onSuccess { bucket ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            bucketDetail = bucket
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "게시글을 불러오지 못했습니다."
                        )
                    }
                }
        }
    }
}
