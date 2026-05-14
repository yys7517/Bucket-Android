package com.bucket.data.datasource.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.domain.model.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val USER_ID = longPreferencesKey("user_id")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val userId: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN]
    }

    val refreshToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN]
    }

    suspend fun saveUserInfo(userInfo: UserInfo) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userInfo.userId
            preferences[ACCESS_TOKEN] = userInfo.accessToken
            preferences[REFRESH_TOKEN] = userInfo.refreshToken
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(ACCESS_TOKEN)
            preferences.remove(REFRESH_TOKEN)
        }
    }
}