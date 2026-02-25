package com.example.handify.core.di

import com.example.handify.core.network.createHttpClient
import com.example.handify.data.remote.api.AuthApi
import com.example.handify.data.repository.AuthRepositoryImpl
import com.example.handify.data.repository.TokenStorage
import com.example.handify.domain.repository.AuthRepository
import com.example.handify.presentation.auth.LoginViewModel
import io.ktor.client.*
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

// TODO: Replace with your actual server URL
const val BASE_URL = "http://10.0.2.2:8080" // Android emulator → localhost

val sharedModule = module {
    single<HttpClient> { createHttpClient() }

    // Auth
    single { AuthApi(get(), BASE_URL) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    // ViewModels
    viewModel { LoginViewModel(get()) }
}
