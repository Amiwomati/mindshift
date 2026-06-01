package com.mindshift.anxiety.data.repository

import com.mindshift.anxiety.data.preferences.UserPreferences
import com.mindshift.anxiety.data.remote.ApiService
import com.mindshift.anxiety.data.remote.models.LoginRequest
import com.mindshift.anxiety.data.remote.models.RegisterRequest
import org.json.JSONObject
import retrofit2.HttpException
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
        } catch (e: HttpException) {
            Result.failure(Exception(parseErrorMessage(e) ?: "Error al registrarse"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            userPreferences.saveUser(response.accessToken, response.user.id, response.user.name)
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(Exception(parseErrorMessage(e) ?: "Error al iniciar sesión"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseErrorMessage(e: HttpException): String? {
        return try {
            val body = e.response()?.errorBody()?.string() ?: return null
            JSONObject(body).optString("message").takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun logout() {
        userPreferences.clear()
    }
}
