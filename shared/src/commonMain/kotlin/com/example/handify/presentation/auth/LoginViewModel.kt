package com.example.handify.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handify.domain.repository.AuthRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val user = authRepository.loginWithGoogle(idToken)
                state = state.copy(isLoading = false, user = user, isLoggedIn = true)
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = e.message ?: "Google login failed")
            }
        }
    }

    fun onError(message: String) {
        state = state.copy(isLoading = false, error = message)
    }

    fun clearError() {
        state = state.copy(error = null)
    }
}
