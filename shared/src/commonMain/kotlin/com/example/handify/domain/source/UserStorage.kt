package com.example.handify.domain.source

import com.example.handify.domain.model.User

interface UserStorage {
    fun save(user: User)
    fun get(): User?
    fun clear()
}
