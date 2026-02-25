package com.example.handify.core.errors

import io.ktor.http.*

sealed class ApiException(
    val statusCode: HttpStatusCode,
    override val message: String
) : Exception(message) {
    class Unauthorized(message: String = "Unauthorized") : ApiException(HttpStatusCode.Unauthorized, message)
    class NotFound(message: String = "Not found") : ApiException(HttpStatusCode.NotFound, message)
    class BadRequest(message: String = "Bad request") : ApiException(HttpStatusCode.BadRequest, message)
    class InternalError(message: String = "Internal server error") : ApiException(HttpStatusCode.InternalServerError, message)
}
