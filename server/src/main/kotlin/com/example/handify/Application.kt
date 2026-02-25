package com.example.handify

import com.example.handify.app.di.serverModule
import com.example.handify.app.plugins.configureRouting
import com.example.handify.app.plugins.configureSecurity
import com.example.handify.app.plugins.configureSerialization
import com.example.handify.app.plugins.configureStatusPages
import com.example.handify.core.database.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(serverModule)
    }

    DatabaseFactory.init()

    configureSerialization()
    configureSecurity()
    configureStatusPages()
    configureRouting()
}
