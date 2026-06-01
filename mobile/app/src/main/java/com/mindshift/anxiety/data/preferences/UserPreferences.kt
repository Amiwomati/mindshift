package com.mindshift.anxiety.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TOKEN_KEY = stringPreferencesKey("access_token")
    private val USER_ID_KEY = intPreferencesKey("user_id")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")
    private val LAST_SYNC_KEY = stringPreferencesKey("last_sync")

    val accessToken: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val userId: Flow<Int?> = context.dataStore.data.map { it[USER_ID_KEY] }
    val userName: Flow<String?> = context.dataStore.data.map { it[USER_NAME_KEY] }
    val lastSync: Flow<String?> = context.dataStore.data.map { it[LAST_SYNC_KEY] }

    suspend fun saveUser(token: String, userId: Int, userName: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId
            prefs[USER_NAME_KEY] = userName
        }
    }

    suspend fun saveLastSync(timestamp: String) {
        context.dataStore.edit { it[LAST_SYNC_KEY] = timestamp }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    suspend fun getTokenOnce(): String? {
        var token: String? = null
        context.dataStore.data.collect {
            token = it[TOKEN_KEY]
            return@collect
        }
        return token
    }

    suspend fun getUserIdOnce(): Int? {
        var id: Int? = null
        context.dataStore.data.collect {
            id = it[USER_ID_KEY]
            return@collect
        }
        return id
    }
}
