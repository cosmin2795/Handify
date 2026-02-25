package com.example.handify.app.plugins

import com.example.handify.core.errors.ApiException
import com.example.handify.core.errors.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<ApiException> { call, cause ->
            call.respond(cause.statusCode, ErrorResponse(cause.statusCode.description, cause.message))
        }
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse("internal_server_error", cause.message ?: "An unexpected error occurred")
            )
        }
    }
}
