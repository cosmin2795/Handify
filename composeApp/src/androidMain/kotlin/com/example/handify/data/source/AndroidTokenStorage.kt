package com.example.handify.data.source

import android.content.Context
import com.example.handify.domain.source.TokenStorage

class AndroidTokenStorage(context: Context) : TokenStorage {

    private val prefs = context.getSharedPreferences("handify_prefs", Context.MODE_PRIVATE)

    override fun save(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    override fun get(): String? = prefs.getString("auth_token", null)
}
