package com.example.handify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.handify.core.di.sharedModule
import com.example.handify.di.appModule
import com.example.handify.navigation.AppNavigation
import com.example.handify.ui.theme.HandifyTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity)
            modules(appModule, sharedModule)
        }

        setContent {
            HandifyTheme {
                AppNavigation()
            }
        }
    }
}
