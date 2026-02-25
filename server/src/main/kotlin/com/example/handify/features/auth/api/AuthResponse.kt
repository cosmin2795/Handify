package com.example.handify.features.auth.api

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val userId: String,
    val email: String,
    val name: String
)
