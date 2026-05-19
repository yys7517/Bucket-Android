package com.bucket.presentation.ui.postcreate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.post.PostDetail
import com.example.domain.model.user.Author
import com.example.domain.usecase.post.CreatePostUseCase
import com.example.domain.usecase.profile.GetMyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostCreateUiState(
    val isCreating: Boolean = false,
)

sealed interface PostCreateEvent {
    data class Created(val post: PostDetail) : PostCreateEvent
    data class Error(val message: String) : PostCreateEvent
}

@HiltViewModel
class PostCreateViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PostCreateUiState())
    val uiState: StateFlow<PostCreateUiState> = _uiState.asStateFlow()

    private val _events = Channel<PostCreateEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun createPost(input: PostCreateInput) {
        if (_uiState.value.isCreating) return

        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true) }

            getMyProfileUseCase()
                .map { profile ->
                    Author(
                        userId = profile.id,
                        username = profile.username,
                        profileImgUrl = profile.profileImgUrl,
                    )
                }
                .fold(
                    onSuccess = { author ->
                        createPostUseCase(
                            goal = input.title.trim(),
                            categoryId = input.categoryId,
                            startDate = input.startDate,
                            memo = input.memo.trim(),
                            author = author,
                        ).onSuccess { post ->
                            _uiState.update { it.copy(isCreating = false) }
                            _events.trySend(PostCreateEvent.Created(post))
                        }.onFailure { throwable ->
                            _uiState.update { it.copy(isCreating = false) }
                            _events.trySend(
                                PostCreateEvent.Error(
                                    throwable.message ?: "게시글 작성에 실패했습니다."
                                )
                            )
                        }
                    },
                    onFailure = { throwable ->
                        _uiState.update { it.copy(isCreating = false) }
                        _events.trySend(
                            PostCreateEvent.Error(
                                throwable.message ?: "내 프로필 정보를 불러오지 못했습니다."
                            )
                        )
                    }
                )
        }
    }
}
