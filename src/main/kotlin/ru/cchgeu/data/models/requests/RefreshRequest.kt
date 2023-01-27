package ru.cchgeu.data.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(
    val userId: Int,
    val refreshToken: String
)
