package com.example.handify.core.di

import com.example.handify.data.remote.api.AuthApi
import com.example.handify.data.remote.api.JobApi
import com.example.handify.domain.source.TokenStorage
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

const val BASE_URL = "http://10.0.2.2:8080"

val sharedModule = module {
    single<HttpClient> {
        val tokenStorage: TokenStorage = get()
        HttpClient {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("KtorClient: $message")
                    }
                }
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            defaultRequest {
                tokenStorage.get()?.let { token ->
                    headers.append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
    }

    single { AuthApi(get(), BASE_URL) }
    single { JobApi(get(), BASE_URL) }
}
