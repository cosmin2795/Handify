package com.example.handify.domain.source

interface TokenStorage {
    fun save(token: String)
    fun get(): String?
}
