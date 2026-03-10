package com.example.handify.data.repository

import com.example.handify.data.remote.api.AuthApi
import com.example.handify.domain.model.User
import com.example.handify.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun loginWithGoogle(idToken: String): User {
        val response = authApi.loginWithGoogle(idToken)
        return User(id = response.userId, email = response.email, name = response.name)
    }
}
