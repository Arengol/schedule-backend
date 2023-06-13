package ru.cchgeu.data.models.requests

data class UpdateEventByIdRequest(
    val id: Int,
    val timeBegin: String,
    val timeEnd: String,
    val date: String,
    val name: String
)
