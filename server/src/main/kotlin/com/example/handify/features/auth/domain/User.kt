package com.example.handify.features.auth.domain

data class User(
    val id: String,
    val email: String,
    val name: String,
    val providerId: String,
    val provider: AuthProvider,
    val createdAt: Long
)

enum class AuthProvider {
    GOOGLE, FACEBOOK
}
