package com.bucket.data.network.di

import com.bucket.data.BuildConfig
import com.bucket.data.datasource.auth.AuthLocalDataSource
import com.bucket.data.network.dto.auth.RefreshTokenResponse
import com.bucket.data.network.dto.common.BaseResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    private val networkJson = Json {
        ignoreUnknownKeys = true    // 서버 응답에 DTO에 없는 필드가 있어도 에러 안 냄
        coerceInputValues = true
        prettyPrint = true
    }

    private fun createKtorClient(expectSuccess: Boolean = true): HttpClient =
        HttpClient(OkHttp) {
            this.expectSuccess = expectSuccess

            install(ContentNegotiation) {
                json(networkJson)
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.tag("DEFAULT_NETWORK").i(message)
                    }
                }
                level = LogLevel.ALL
            }
        }

    @Provides
    @Singleton
    @DefaultNetwork
    fun provideDefaultHttpClient(
        authLocalDataSource: AuthLocalDataSource
    ): HttpClient {
        val refreshClient = createKtorClient(expectSuccess = false).config {
            defaultRequest {
                url(BuildConfig.BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }

        return createKtorClient().config {
            defaultRequest {
                url(BuildConfig.BASE_URL)
                contentType(ContentType.Application.Json)
            }
            install(Auth) {
                bearer {
                    cacheTokens = false

                    loadTokens {
                        val accessToken = authLocalDataSource.accessToken.first()
                        val refreshToken = authLocalDataSource.refreshToken.first()

                        if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                            null
                        } else {
                            BearerTokens(
                                accessToken = accessToken,
                                refreshToken = refreshToken
                            )
                        }
                    }

                    refreshTokens {
                        val refreshToken = authLocalDataSource.refreshToken.first()

                        if (refreshToken.isNullOrBlank()) {
                            authLocalDataSource.clear()
                            return@refreshTokens null
                        }

                        val response = runCatching {
                            refreshClient.post("auth/refresh") {
                                header(HttpHeaders.Authorization, "Bearer $refreshToken")
                            }
                        }.getOrElse { throwable ->
                            Timber.e(throwable, "Token refresh request failed.")
                            authLocalDataSource.clear()
                            return@refreshTokens null
                        }

                        if (!response.status.isSuccess()) {
                            val errorBody = response.bodyAsText()
                            Timber.e("Token refresh failed with status=%s body=%s", response.status, errorBody)
                            authLocalDataSource.clear()
                            return@refreshTokens null
                        }

                        val refreshResponse = runCatching {
                            response.body<BaseResponse<RefreshTokenResponse>>()
                        }.getOrElse { throwable ->
                            Timber.e(throwable, "Token refresh response parsing failed.")
                            authLocalDataSource.clear()
                            return@refreshTokens null
                        }

                        authLocalDataSource.saveAccessToken(refreshResponse.data.accessToken)

                        BearerTokens(
                            accessToken = refreshResponse.data.accessToken,
                            refreshToken = refreshToken
                        )
                    }

                    sendWithoutRequest { request ->
                        val path = request.url.encodedPath
                        !path.contains("auth/kakao") &&
                            !path.contains("auth/logout") &&
                            !path.contains("auth/refresh")
                    }
                }
            }
        }
    }
}
