package com.example.handify.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.handify.domain.repository.AuthRepository
import com.example.handify.domain.source.GoogleAuthSource
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val googleAuthSource: GoogleAuthSource
) : ViewModel() {

    var state by mutableStateOf(LoginState())
        private set

    fun loginWithGoogle() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val idToken = googleAuthSource.getIdToken()
                val user = authRepository.loginWithGoogle(idToken)
                state = state.copy(isLoading = false, user = user, isLoggedIn = true)
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = e.message ?: "Google login failed")
            }
        }
    }

    fun clearError() {
        state = state.copy(error = null)
    }
}
