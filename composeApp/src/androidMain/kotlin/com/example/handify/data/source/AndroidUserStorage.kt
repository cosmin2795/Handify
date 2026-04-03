package com.example.handify.data.source

import android.content.Context
import com.example.handify.domain.model.User
import com.example.handify.domain.source.UserStorage

class AndroidUserStorage(context: Context) : UserStorage {

    private val prefs = context.getSharedPreferences("handify_user", Context.MODE_PRIVATE)

    override fun save(user: User) {
        prefs.edit()
            .putString("id", user.id)
            .putString("email", user.email)
            .putString("name", user.name)
            .apply()
    }

    override fun get(): User? {
        val id = prefs.getString("id", null) ?: return null
        val email = prefs.getString("email", null) ?: return null
        val name = prefs.getString("name", null) ?: return null
        return User(id = id, email = email, name = name)
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }
}
