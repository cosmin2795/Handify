package com.example.handify.data.repository

import com.example.handify.data.remote.api.AuthApi
import com.example.handify.domain.model.User
import com.example.handify.domain.repository.AuthRepository
import com.example.handify.domain.source.TokenStorage

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun loginWithGoogle(idToken: String): User {
        val response = authApi.loginWithGoogle(idToken)
        tokenStorage.save(response.token)
        return User(id = response.userId, email = response.email, name = response.name)
    }
}
