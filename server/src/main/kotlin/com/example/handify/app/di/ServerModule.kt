package com.example.handify.app.di

import com.example.handify.features.auth.data.UserRepositoryImpl
import com.example.handify.features.auth.domain.LoginWithGoogleUseCase
import com.example.handify.features.auth.domain.UserRepository
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val serverModule = module {
    // HTTP Client for calling external APIs (Google)
    single<HttpClient> {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    // Auth
    single<UserRepository> { UserRepositoryImpl() }
    single { LoginWithGoogleUseCase(get(), get()) }
}
