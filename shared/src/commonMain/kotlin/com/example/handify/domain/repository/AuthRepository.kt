package com.example.handify.domain.repository

import com.example.handify.domain.model.User

interface AuthRepository {
    suspend fun loginWithGoogle(idToken: String): User
    suspend fun loginWithFacebook(accessToken: String): User
    fun saveAuthToken(token: String)
    fun getAuthToken(): String?
    fun clearAuthToken()
}
