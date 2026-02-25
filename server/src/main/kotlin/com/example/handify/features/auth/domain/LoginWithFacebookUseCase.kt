package com.example.handify.features.auth.domain

import com.example.handify.core.errors.ApiException
import com.example.handify.core.security.JwtConfig
import com.example.handify.features.auth.api.AuthResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

@Serializable
private data class FacebookUserInfo(
    val id: String,
    val name: String? = null,
    val email: String? = null,
    val error: FacebookError? = null
)

@Serializable
private data class FacebookError(
    val message: String,
    val type: String,
    val code: Int
)

class LoginWithFacebookUseCase(
    private val userRepository: UserRepository,
    private val httpClient: HttpClient
) {
    suspend operator fun invoke(accessToken: String): AuthResponse {
        // Verify the Facebook access token using Graph API
        val userInfo = try {
            httpClient.get("https://graph.facebook.com/me") {
                parameter("fields", "id,name,email")
                parameter("access_token", accessToken)
            }.body<FacebookUserInfo>()
        } catch (e: Exception) {
            throw ApiException.Unauthorized("Invalid Facebook token")
        }

        if (userInfo.error != null) {
            throw ApiException.Unauthorized("Invalid Facebook token: ${userInfo.error.message}")
        }

        val providerId = userInfo.id
        val name = userInfo.name ?: "Facebook User"
        val email = userInfo.email ?: "$providerId@facebook.placeholder"

        val user = userRepository.findByProviderId(providerId, AuthProvider.FACEBOOK)
            ?: userRepository.createUser(
                email = email,
                name = name,
                providerId = providerId,
                provider = AuthProvider.FACEBOOK
            )

        val token = JwtConfig.generateToken(user.id, user.email)
        return AuthResponse(token = token, userId = user.id, email = user.email, name = user.name)
    }
}
