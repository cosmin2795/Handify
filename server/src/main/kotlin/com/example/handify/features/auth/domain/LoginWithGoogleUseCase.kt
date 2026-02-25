package com.example.handify.features.auth.domain

import com.example.handify.core.errors.ApiException
import com.example.handify.core.security.JwtConfig
import com.example.handify.features.auth.api.AuthResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

@Serializable
private data class GoogleTokenInfo(
    val sub: String,
    val email: String,
    val name: String? = null,
    val given_name: String? = null,
    val family_name: String? = null,
    val error_description: String? = null
)

class LoginWithGoogleUseCase(
    private val userRepository: UserRepository,
    private val httpClient: HttpClient
) {
    suspend operator fun invoke(idToken: String): AuthResponse {
        // Verify the Google ID token using Google's tokeninfo endpoint
        val tokenInfo = try {
            httpClient.get("https://oauth2.googleapis.com/tokeninfo") {
                parameter("id_token", idToken)
            }.body<GoogleTokenInfo>()
        } catch (e: Exception) {
            throw ApiException.Unauthorized("Invalid Google token")
        }

        if (tokenInfo.error_description != null) {
            throw ApiException.Unauthorized("Invalid Google token: ${tokenInfo.error_description}")
        }

        val sub = tokenInfo.sub
        val email = tokenInfo.email
        val name = tokenInfo.name
            ?: "${tokenInfo.given_name.orEmpty()} ${tokenInfo.family_name.orEmpty()}".trim()
            .ifEmpty { email }

        val user = userRepository.findByProviderId(sub, AuthProvider.GOOGLE)
            ?: userRepository.createUser(
                email = email,
                name = name,
                providerId = sub,
                provider = AuthProvider.GOOGLE
            )

        val token = JwtConfig.generateToken(user.id, user.email)
        return AuthResponse(token = token, userId = user.id, email = user.email, name = user.name)
    }
}
