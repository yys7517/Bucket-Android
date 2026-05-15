package com.bucket.data.network.di

import com.bucket.data.BuildConfig
import com.bucket.data.datasource.auth.AuthLocalDataSource
import com.bucket.data.network.dto.auth.RefreshTokenResponse
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
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
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

    private fun createKtorClient(): HttpClient =
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(networkJson)
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.tag("DEFAULT_NETWORK").i(message)
                    }
                }
                level = LogLevel.BODY
            }
        }

    @Provides
    @Singleton
    @DefaultNetwork
    fun provideDefaultHttpClient(
        authLocalDataSource: AuthLocalDataSource
    ): HttpClient =
        createKtorClient().config {
            defaultRequest {
                url(BuildConfig.BASE_URL)
                contentType(ContentType.Application.Json)
            }
            install(Auth) {
                bearer {
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
                        val refreshToken = oldTokens?.refreshToken

                        if (refreshToken.isNullOrBlank()) {
                            authLocalDataSource.clear()
                            return@refreshTokens null
                        }

                        runCatching {
                            val refreshResponse = client.post("auth/refresh") {
                                markAsRefreshTokenRequest()
                                bearerAuth(refreshToken)
                            }.body<RefreshTokenResponse>()

                            authLocalDataSource.saveAccessToken(refreshResponse.accessToken)

                            BearerTokens(
                                accessToken = refreshResponse.accessToken,
                                refreshToken = refreshToken
                            )
                        }.getOrElse { throwable ->
                            Timber.e(throwable, "Token refresh failed.")
                            authLocalDataSource.clear()
                            null
                        }
                    }

                    sendWithoutRequest { request ->
                        val path = request.url.encodedPath
                        !path.contains("auth/kakao") && !path.contains("auth/logout")
                    }
                }
            }
        }
}
