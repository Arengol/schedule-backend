package ru.cchgeu.data.models.requests

import kotlinx.serialization.Serializable

@Serializable
data class AddNewEventRequest(
    val timeBegin: String,
    val timeEnd: String,
    val date: String,
    val name: String
)
