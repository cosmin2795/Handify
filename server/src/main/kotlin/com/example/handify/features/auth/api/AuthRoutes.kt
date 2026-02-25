package com.example.handify.features.auth.api

import com.example.handify.core.errors.ApiException
import com.example.handify.core.errors.ErrorResponse
import com.example.handify.features.auth.domain.LoginWithFacebookUseCase
import com.example.handify.features.auth.domain.LoginWithGoogleUseCase
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val loginWithGoogle: LoginWithGoogleUseCase by inject()
    val loginWithFacebook: LoginWithFacebookUseCase by inject()

    route("/auth") {
        post("/google") {
            val request = call.receive<GoogleAuthRequest>()
            if (request.idToken.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("bad_request", "idToken is required"))
                return@post
            }
            try {
                val response = loginWithGoogle(request.idToken)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: ApiException) {
                call.respond(e.statusCode, ErrorResponse(e.statusCode.description, e.message))
            }
        }

        post("/facebook") {
            val request = call.receive<FacebookAuthRequest>()
            if (request.accessToken.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("bad_request", "accessToken is required"))
                return@post
            }
            try {
                val response = loginWithFacebook(request.accessToken)
                call.respond(HttpStatusCode.OK, response)
            } catch (e: ApiException) {
                call.respond(e.statusCode, ErrorResponse(e.statusCode.description, e.message))
            }
        }
    }
}
