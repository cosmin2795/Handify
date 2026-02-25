package com.example.handify.features.auth.domain

interface UserRepository {
    suspend fun findById(id: String): User?
    suspend fun findByEmail(email: String): User?
    suspend fun findByProviderId(providerId: String, provider: AuthProvider): User?
    suspend fun createUser(
        email: String,
        name: String,
        providerId: String,
        provider: AuthProvider
    ): User
}
