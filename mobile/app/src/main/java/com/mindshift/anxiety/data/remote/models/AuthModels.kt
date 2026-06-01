package com.mindshift.anxiety.data.remote.models

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UserDto(
    val id: Int,
    val name: String,
    val email: String
)

data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String,
    val user: UserDto
)
