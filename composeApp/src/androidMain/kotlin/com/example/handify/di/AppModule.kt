package com.example.handify.di

import com.example.handify.R
import com.example.handify.data.remote.source.AndroidGoogleAuthSource
import com.example.handify.data.repository.AuthRepositoryImpl
import com.example.handify.domain.repository.AuthRepository
import com.example.handify.domain.source.GoogleAuthSource
import com.example.handify.presentation.auth.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<GoogleAuthSource> {
        AndroidGoogleAuthSource(
            context = androidContext(),
            webClientId = androidContext().getString(R.string.google_web_client_id)
        )
    }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    viewModel { LoginViewModel(get(), get()) }
}
