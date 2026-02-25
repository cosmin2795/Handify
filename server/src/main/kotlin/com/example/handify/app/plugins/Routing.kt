package com.example.handify.app.plugins

import com.example.handify.features.auth.api.authRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/api") {
            authRoutes()
        }
    }
}
