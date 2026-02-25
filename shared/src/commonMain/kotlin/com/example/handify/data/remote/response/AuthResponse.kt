package com.example.handify.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val userId: String,
    val email: String,
    val name: String
)
