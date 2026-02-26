package com.example.handify

import android.app.Application
import com.example.handify.core.di.sharedModule
import com.example.handify.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HandifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@HandifyApplication)
            modules(appModule, sharedModule)
        }
    }
}
