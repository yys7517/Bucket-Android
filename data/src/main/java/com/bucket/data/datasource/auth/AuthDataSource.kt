package com.bucket.data.datasource.auth

import com.bucket.data.network.dto.auth.AuthResponse
import com.bucket.data.network.dto.common.BaseResponse

interface AuthDataSource {
    suspend fun loginWithKakao(accessToken: String): BaseResponse<AuthResponse>
}
