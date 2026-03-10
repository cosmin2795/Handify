package com.example.handify.domain.repository

import com.example.handify.domain.model.User

interface AuthRepository {
    suspend fun loginWithGoogle(idToken: String): User
}
