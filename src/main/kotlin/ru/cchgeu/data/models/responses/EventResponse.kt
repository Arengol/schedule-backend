package ru.cchgeu.data.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class EventResponse(
    val id: Int,
    val timeBegin: String,
    val timeEnd: String,
    val date: String,
    val name: String
)
