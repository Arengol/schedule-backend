package ru.cchgeu.security.token

data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)
