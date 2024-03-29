package ru.cchgeu.data.models.requests

import kotlinx.serialization.Serializable
import ru.cchgeu.data.models.InviteCode

@Serializable
data class SignUpRequest(
    val username: String,
    val password: String,
    val status: Int,
    val inviteCode: String
)
