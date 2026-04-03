package com.example.handify.data.repository

import com.example.handify.data.remote.api.AuthApi
import com.example.handify.domain.model.User
import com.example.handify.domain.repository.AuthRepository
import com.example.handify.domain.source.TokenStorage
import com.example.handify.domain.source.UserStorage

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage,
    private val userStorage: UserStorage
) : AuthRepository {

    override suspend fun loginWithGoogle(idToken: String): User {
        val response = authApi.loginWithGoogle(idToken)
        tokenStorage.save(response.token)
        val user = User(id = response.userId, email = response.email, name = response.name)
        userStorage.save(user)
        return user
    }
}
