package com.mindshift.anxiety.data.remote

import com.mindshift.anxiety.data.remote.models.AuthResponse
import com.mindshift.anxiety.data.remote.models.LoginRequest
import com.mindshift.anxiety.data.remote.models.RegisterRequest
import com.mindshift.anxiety.data.remote.models.SyncRequest
import com.mindshift.anxiety.data.remote.models.SyncResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/clicks/sync")
    suspend fun syncClicks(
        @Header("Authorization") token: String,
        @Body request: SyncRequest
    ): SyncResponse
}
