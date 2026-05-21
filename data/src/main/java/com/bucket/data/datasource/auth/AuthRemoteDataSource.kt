package com.bucket.data.datasource.auth

import com.bucket.data.network.di.DefaultNetwork
import com.bucket.data.network.dto.auth.AuthRequest
import com.bucket.data.network.dto.auth.AuthResponse
import com.bucket.data.network.dto.common.BaseResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    @param:DefaultNetwork private val client: HttpClient
) : AuthDataSource {
    override suspend fun loginWithKakao(accessToken: String): BaseResponse<AuthResponse> {
        val response = client.post("auth/kakao") {
            setBody(AuthRequest(accessToken = accessToken))
        }

        if (!response.status.isSuccess()) {
            throw IllegalStateException(response.bodyAsText())
        }

        return response.body()
    }

    override suspend fun logout() {
        val response = client.post("auth/logout")

        if (!response.status.isSuccess()) {
            throw IllegalStateException(response.bodyAsText())
        }

        if (response.status.value == 200) return

        val logoutResponse = response.body<BaseResponse<String>>()
        if (logoutResponse.data != "Success" && logoutResponse.code != 200) {
            throw IllegalStateException(logoutResponse.message)
        }
    }
}
