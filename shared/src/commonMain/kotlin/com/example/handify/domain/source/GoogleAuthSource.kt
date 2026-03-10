package com.example.handify.domain.source

interface GoogleAuthSource {
    suspend fun getIdToken(): String
}
