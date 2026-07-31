package com.example.luxeval

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform