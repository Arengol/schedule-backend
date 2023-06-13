package ru.cchgeu.data.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class MentorScheduleByNameRequest(
    val mentor: String
)
