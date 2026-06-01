package com.mindshift.anxiety.data.repository

import com.mindshift.anxiety.data.preferences.UserPreferences
import com.mindshift.anxiety.data.remote.ApiService
import com.mindshift.anxiety.data.remote.models.LoginRequest
import com.mindshift.anxiety.data.remote.models.RegisterRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {
    suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.register(RegisterRequest(name, email, password))
            userPreferences.saveUser(response.accessToken, response.user.id, response.user.name)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            userPreferences.saveUser(response.accessToken, response.user.id, response.user.name)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        userPreferences.clear()
    }
}
