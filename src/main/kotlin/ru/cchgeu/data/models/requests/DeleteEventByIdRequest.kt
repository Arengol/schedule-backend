package ru.cchgeu.data.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class DeleteEventByIdRequest(
    val eventId: Int
)
