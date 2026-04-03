package com.example.handify.di

import com.example.handify.R
import com.example.handify.data.remote.source.AndroidGoogleAuthSource
import com.example.handify.data.repository.AuthRepositoryImpl
import com.example.handify.data.repository.JobRepositoryImpl
import com.example.handify.data.source.AndroidAddressStorage
import com.example.handify.data.source.AndroidLocationSource
import com.example.handify.data.source.AndroidTokenStorage
import com.example.handify.data.source.AndroidUserStorage
import com.example.handify.domain.repository.AuthRepository
import com.example.handify.domain.repository.JobRepository
import com.example.handify.domain.source.AddressStorage
import com.example.handify.domain.source.GoogleAuthSource
import com.example.handify.domain.source.LocationSource
import com.example.handify.domain.source.TokenStorage
import com.example.handify.domain.source.UserStorage
import com.example.handify.presentation.auth.LoginViewModel
import com.example.handify.presentation.job.JobListViewModel
import com.example.handify.presentation.job.MyJobsViewModel
import com.example.handify.presentation.job.PostJobViewModel
import com.example.handify.presentation.location.LocationViewModel
import com.example.handify.presentation.profile.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<TokenStorage> { AndroidTokenStorage(androidContext()) }
    single<UserStorage> { AndroidUserStorage(androidContext()) }
    single<LocationSource> { AndroidLocationSource(androidContext()) }
    single<AddressStorage> { AndroidAddressStorage(androidContext()) }

    single<GoogleAuthSource> {
        AndroidGoogleAuthSource(
            context = androidContext(),
            webClientId = androidContext().getString(R.string.google_web_client_id)
        )
    }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single<JobRepository> { JobRepositoryImpl(get()) }

    viewModel { LocationViewModel(get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { JobListViewModel(get(), get(), get()) }
    viewModel { MyJobsViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { PostJobViewModel(get()) }
}
