package ru.cchgeu.data.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class AssociateMentorRequest(
    val userId: Int,
    val mentorName: String
)
