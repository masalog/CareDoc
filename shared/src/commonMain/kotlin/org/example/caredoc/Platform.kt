package org.example.caredoc

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform