package com.example.handify.data.repository

import android.content.Context
import android.content.SharedPreferences

class AndroidTokenStorage(context: Context) : TokenStorage {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("handify_prefs", Context.MODE_PRIVATE)

    override fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    override fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    override fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    companion object {
        private const val KEY_TOKEN = "auth_token"
    }
}
