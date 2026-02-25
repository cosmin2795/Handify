package com.example.handify.data.remote.api

import com.example.handify.data.remote.response.AuthResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
private data class GoogleAuthRequest(val idToken: String)

@Serializable
private data class FacebookAuthRequest(val accessToken: String)

class AuthApi(private val client: HttpClient, private val baseUrl: String) {

    suspend fun loginWithGoogle(idToken: String): AuthResponse =
        client.post("$baseUrl/api/auth/google") {
            contentType(ContentType.Application.Json)
            setBody(GoogleAuthRequest(idToken))
        }.body()

    suspend fun loginWithFacebook(accessToken: String): AuthResponse =
        client.post("$baseUrl/api/auth/facebook") {
            contentType(ContentType.Application.Json)
            setBody(FacebookAuthRequest(accessToken))
        }.body()
}
