package com.example.handify.core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtConfig {
    private val SECRET = System.getenv("JWT_SECRET") ?: "handify-jwt-secret-change-in-production"
    const val ISSUER = "handify"
    const val AUDIENCE = "handify-users"
    private const val VALIDITY_MS = 86_400_000L

    val algorithm: Algorithm = Algorithm.HMAC256(SECRET)

    fun generateToken(userId: String, email: String): String =
        JWT.create()
            .withAudience(AUDIENCE)
            .withIssuer(ISSUER)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_MS))
            .sign(algorithm)
}
