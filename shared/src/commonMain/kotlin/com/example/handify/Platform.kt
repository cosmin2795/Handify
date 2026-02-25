package com.example.handify

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform