package com.bucket.presentation.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.user.UserInfo
import com.example.domain.usecase.auth.LoginWithKakaoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithKakaoUseCase: LoginWithKakaoUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun loginWithKakao(context: Context) {
        if (_uiState.value.isLoading) return

        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            loginWithKakaoTalk(context)
        } else {
            loginWithKakaoAccount(context)
        }
    }

    private fun loginWithKakaoTalk(context: Context) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            when {
                token != null -> requestServerLogin(token)
                error is ClientError && error.reason == ClientErrorCause.Cancelled -> {
                    onLoginFailure("카카오 로그인이 취소되었어요.")
                }
                error != null -> loginWithKakaoAccount(context)
                else -> onLoginFailure("카카오 로그인에 실패했어요.")
            }
        }
    }

    private fun loginWithKakaoAccount(context: Context) {
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            when {
                token != null -> requestServerLogin(token)
                error is ClientError && error.reason == ClientErrorCause.Cancelled -> {
                    onLoginFailure("카카오 로그인이 취소되었어요.")
                }
                error != null -> onLoginFailure(error.message ?: "카카오 로그인에 실패했어요.")
                else -> onLoginFailure("카카오 로그인에 실패했어요.")
            }
        }
    }

    private fun requestServerLogin(token: OAuthToken) {
        Timber.d(
            "Kakao login success. accessToken=%s refreshToken=%s accessTokenExpiresAt=%s refreshTokenExpiresAt=%s idToken=%s scopes=%s",
            token.accessToken,
            token.refreshToken,
            token.accessTokenExpiresAt,
            token.refreshTokenExpiresAt,
            token.idToken,
            token.scopes
        )

        viewModelScope.launch {
            loginWithKakaoUseCase(token.accessToken)
                .onSuccess {
                    Timber.d("Server kakao login success.")
                    onLoginSuccess(it)
                }
                .onFailure { throwable ->
                    Timber.e(throwable, "Server kakao login failed.")
                    onLoginFailure(throwable.message ?: "서버 로그인에 실패했어요.")
                }
        }
    }

    private fun onLoginSuccess(userInfo: UserInfo) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isLoggedIn = true,
                errorMessage = null
            )
        }
    }

    private fun onLoginFailure(message: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                isLoggedIn = false,
                errorMessage = message
            )
        }
    }
}

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
)
