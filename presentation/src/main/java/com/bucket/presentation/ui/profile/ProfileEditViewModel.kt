package com.bucket.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.profile.ProfileUpdateResult
import com.example.domain.usecase.profile.UpdateMyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileEditUiState(
    val isSaving: Boolean = false,
)

sealed interface ProfileEditEvent {
    data class SaveSucceeded(val profile: ProfileUpdateResult) : ProfileEditEvent
    data class Error(val message: String) : ProfileEditEvent
}

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val updateMyProfileUseCase: UpdateMyProfileUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState.asStateFlow()

    private val _events = Channel<ProfileEditEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun saveProfile(
        username: String,
        email: String,
        introduction: String,
    ) {
        if (_uiState.value.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            updateMyProfileUseCase(
                username = username,
                email = email,
                introduction = introduction,
            ).onSuccess { profile ->
                _uiState.update { it.copy(isSaving = false) }
                _events.trySend(ProfileEditEvent.SaveSucceeded(profile))
            }.onFailure { throwable ->
                _uiState.update { it.copy(isSaving = false) }
                _events.trySend(
                    ProfileEditEvent.Error(
                        throwable.message ?: "프로필 저장에 실패했습니다."
                    )
                )
            }
        }
    }
}
