package com.example.handify.data.repository

import com.example.handify.data.remote.api.AuthApi
import com.example.handify.domain.model.User
import com.example.handify.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun loginWithGoogle(idToken: String): User {
        val response = authApi.loginWithGoogle(idToken)
        tokenStorage.saveToken(response.token)
        return User(id = response.userId, email = response.email, name = response.name)
    }

    override fun saveAuthToken(token: String) = tokenStorage.saveToken(token)

    override fun getAuthToken(): String? = tokenStorage.getToken()

    override fun clearAuthToken() = tokenStorage.clearToken()
}

/** Platform-specific token storage (SharedPreferences on Android, Keychain on iOS). */
interface TokenStorage {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
}
