package com.example.handify.di

import com.example.handify.data.repository.AndroidTokenStorage
import com.example.handify.data.repository.TokenStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single<TokenStorage> { AndroidTokenStorage(androidContext()) }
}
