package ru.cchgeu.data.models.responses

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleResponse (
    val group: String,
    val time: String,
    val dayWeek: String,
    val weekType: Int,
    val name: String,
    val classType: String,
    val auditory: List<String>?,
    val mentor: List<String>?
)

//fun ScheduleResponse.ToScheduleResponse() = ScheduleResponse(
//
//)