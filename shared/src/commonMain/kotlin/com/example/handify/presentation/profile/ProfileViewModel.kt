package com.example.handify.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.handify.domain.source.TokenStorage
import com.example.handify.domain.source.UserStorage

class ProfileViewModel(
    private val userStorage: UserStorage,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    var state by mutableStateOf(ProfileState())
        private set

    init {
        state = state.copy(user = userStorage.get())
    }

    fun logOut() {
        tokenStorage.clear()
        userStorage.clear()
        state = ProfileState()
    }
}
