package com.example.handify.features.auth.api

import kotlinx.serialization.Serializable

@Serializable
data class GoogleAuthRequest(val idToken: String)

@Serializable
data class FacebookAuthRequest(val accessToken: String)
