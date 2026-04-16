package com.example.handify.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.handify.core.session.SessionEventBus
import com.example.handify.domain.source.TokenStorage
import com.example.handify.ui.screen.LoginScreen
import com.example.handify.ui.screen.MainScreen
import org.koin.compose.koinInject

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val tokenStorage: TokenStorage = koinInject()
    val sessionEventBus: SessionEventBus = koinInject()
    val startDestination = if (tokenStorage.get() != null) Routes.Home.route else Routes.Login.route

    LaunchedEffect(Unit) {
        sessionEventBus.unauthorized.collect {
            navController.navigate(Routes.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Home.route) {
            MainScreen(
                onLogOut = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
