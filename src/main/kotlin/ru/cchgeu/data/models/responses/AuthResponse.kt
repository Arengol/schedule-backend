package ru.cchgeu.data.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val userId: Int,
    val accessToken: String,
    val refreshToken: String
)
