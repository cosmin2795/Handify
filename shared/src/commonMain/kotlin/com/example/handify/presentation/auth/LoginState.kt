package com.example.handify.presentation.auth

import com.example.handify.domain.model.User

data class LoginState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)
